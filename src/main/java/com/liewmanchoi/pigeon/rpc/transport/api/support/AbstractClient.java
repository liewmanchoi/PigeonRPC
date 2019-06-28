package com.liewmanchoi.pigeon.rpc.transport.api.support;

import com.liewmanchoi.pigeon.rpc.common.context.RPCThreadSharedContext;
import com.liewmanchoi.pigeon.rpc.common.domain.Message;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.GlobalConfig;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import com.liewmanchoi.pigeon.rpc.transport.api.Client;
import com.liewmanchoi.pigeon.rpc.transport.api.converter.AbstractClientMessageConverter;
import com.liewmanchoi.pigeon.rpc.transport.api.converter.MessageConverter;
import com.liewmanchoi.pigeon.rpc.transport.pigeon.constant.PigeonConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;


/**
 * Client抽象基类
 * 各个实现类的不同之处仅仅在于添加的ChannelHandler的不同
 *
 * @author wangsheng
 * @date 2019/6/26
 */
@Slf4j
public abstract class AbstractClient implements Client {
    // TODO: 使用EventLoop.schedule()进行调度
    /**
     * 延时任务调度线程池（线程池中只有一个线程）
     */
    private static ScheduledExecutorService retryExecutor = Executors.newSingleThreadScheduledExecutor();

    @Getter
    private ServiceURL serviceURL;

    private GlobalConfig globalConfig;
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;
    private volatile Channel futureChannel;
    private volatile boolean initialized = false;
    private volatile boolean destroyed = false;
    private volatile boolean closedFromOuter = false;
    private MessageConverter messageConverter;
    private ConnectionRetry connectionRetry = new ConnectionRetry();

    /**
     * 初始化ChannelPipeline
     *
     * @return io.netty.channel.ChannelInitializer
     * @date 2019/6/27
     */
    protected abstract ChannelInitializer initPipeline();

    /**
     * 初始化Message converter
     *
     * @return ClientMessageConverter
     * @date 2019/6/27
     */
    protected abstract AbstractClientMessageConverter initConverter();

    /**
     * 初始化连接（包括建立连接）
     */
    public void init(GlobalConfig globalConfig, ServiceURL serviceURL) {
        this.globalConfig = globalConfig;
        this.serviceURL = serviceURL;

        synchronized (AbstractClient.class) {
            if (initialized) {
                return;
            }

            this.messageConverter = initConverter();
            this.eventLoopGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
            this.bootstrap = new Bootstrap();
            this.bootstrap.group(eventLoopGroup)
                    .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                    .handler(initPipeline())
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true);

            // 建立连接
            connect();
        }
    }

    /**
     * 建立连接
     */
    private synchronized void connect(){
        ChannelFuture channelFuture;
        String address = getServiceURL().getAddress();
        String host = address.split(":")[0];
        int port = Integer.valueOf(address.split(":")[1]);
        try {
            channelFuture = bootstrap.connect(host, port).sync();
            this.futureChannel = channelFuture.channel();

            log.info("客户端初始化完毕");
        } catch (InterruptedException e) {
            log.error("未能成功连接到服务器", e);
            // 调用异常处理函数（开启无数次重连尝试，如果重连过程中发生异常，则关闭client）
            handleException(e);
        }

        log.info("客户端已连接至-{}", address);
        initialized = true;
        destroyed = false;
    }

    @Override
    public void handleException(Throwable throwable) {
        // destroyed设置为true，在异常处理期间拒绝接受任务提交
        destroyed = true;
        try {
            // 开始进行重连（无数次尝试，直至成功）
            if (destroyed) {
                connectionRetry.run();
            }
        } catch (Exception e) {
            // 重连过程中如果发生异常，则关闭客户端，放弃尝试
            log.error("重连过程中发生错误，客户端接下来将被关闭", e);
            close();
            // 抛出自定义RPCException
            throw new RPCException(ErrorEnum.CONNECT_TO_SERVER_FAILURE, "客户端多次重试建立连接的过程中发生错误，放弃重试，并且关闭客户端");
        }
    }

    /**
     * 关闭client
     *
     * 要点：必须满足幂等性，保证不会重复关闭endpoint
     */
    @Override
    public void close() {
        if (futureChannel != null && futureChannel.isOpen()) {
            try {
                futureChannel.close().sync();
            } catch (InterruptedException e) {
                log.error("关闭client过程中发生中断异常", e);
            } finally {
                destroyed = true;
                closedFromOuter = true;

                if (eventLoopGroup != null &&
                        !eventLoopGroup.isTerminated() &&
                        !eventLoopGroup.isShutdown() &&
                        !eventLoopGroup.isShuttingDown()) {
                    eventLoopGroup.shutdownGracefully();
                }
            }
        }
    }

    /**
     * 提交并发送RPC调用请求
     */
    @Override
    public Future<RPCResponse> submit(RPCRequest request) {
        if (!initialized) {
            connect();
        }

        if (destroyed || closedFromOuter) {
            throw new RPCException(ErrorEnum.SUBMIT_AFTER_ENDPOINT_CLOSED, "当前client-{}关闭后仍然在提交任务",
                    serviceURL.getAddress());
        }

        log.info("客户端发起请求：{}，请求的服务器-{}", request, serviceURL.getAddress());
        CompletableFuture<RPCResponse> responseCompletableFuture = new CompletableFuture<>();
        // 缓存结果
        RPCThreadSharedContext.registerResponseFuture(request.getRequestId(), responseCompletableFuture);
        // 将request对象封装成Message对象
        Object data = messageConverter.convertToObject(Message.builderRequest(request));
        // 发送message对象
        log.info("即将发送请求消息-{}", serviceURL.getAddress());
        futureChannel.writeAndFlush(data);

        // 返回异步调用结果
        return responseCompletableFuture;
    }



    @Override
    public void handleCallbackRequest(RPCRequest request, ChannelHandlerContext ctx) {
        // TODO: 待完成handleCallbackRequest功能
    }

    @Override
    public void handleRPCResponse(RPCResponse response) {
        // 将缓存中取出对应的Response future，并设置为相应的值
        CompletableFuture<RPCResponse> future =
                RPCThreadSharedContext.getAndRemoveResponseFuture(response.getRequestId());
        future.complete(response);
    }

    protected GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    @Override
    public void updateServiceConfig(ServiceURL serviceURL) {
        this.serviceURL = serviceURL;
    }

    @Override
    public boolean isAvailable() {
        return initialized && !destroyed;
    }

    private class ConnectionRetry implements Runnable {
        @Override
        public void run() {
            if (!closedFromOuter) {
                try {
                    // 先关闭原有的连接
                    if (futureChannel != null && futureChannel.isOpen()) {
                        futureChannel.close().sync();
                        log.info("关闭旧的连接...");
                    }
                    // 开始建立连接
                    log.info("开始尝试重新建立连接...");
                    connect();
                } catch (InterruptedException e) {
                    // 开始进行无限次重连尝试
                    log.warn("建立重连失败，{}秒后开始重试", PigeonConstant.HEART_BEAT_TIME_OUT);
                    // 利用延时任务调度线程池开始周期性任务调度
                    retryExecutor.schedule(connectionRetry, PigeonConstant.HEART_BEAT_TIME_OUT, TimeUnit.SECONDS);
                }
            } else {
                log.info("外部服务器已经关闭，无法进行重连操作...");
            }
        }
    }
}
