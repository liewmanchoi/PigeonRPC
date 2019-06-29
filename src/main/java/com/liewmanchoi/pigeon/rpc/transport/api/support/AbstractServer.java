package com.liewmanchoi.pigeon.rpc.transport.api.support;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.config.GlobalConfig;
import com.liewmanchoi.pigeon.rpc.transport.api.Server;
import com.liewmanchoi.pigeon.rpc.transport.api.converter.MessageConverter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author wangsheng
 * @date 2019/6/28
 */
@Slf4j
public abstract class AbstractServer implements Server {
    @Getter
    private GlobalConfig globalConfig;
    private ChannelInitializer channelInitializer;
    private MessageConverter messageConverter;
    private ChannelFuture channelFuture;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void init(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        this.channelInitializer = initPipeline();
        this.messageConverter = initMessageConverter();
    }

    /**
     * 初始化ChannelPipeline，设置ChannelHandler(s)
     * @return ChanelInitializer
     */
    protected abstract ChannelInitializer initPipeline();

    /**
     * 初始化消息转换器
     * @return MessageConverter
     */
    protected abstract MessageConverter initMessageConverter();

    @Override
    public void start() {
        boolean supportEpoll = Epoll.isAvailable();
        log.info("服务器是否支持Epoll调用-{}", supportEpoll);
        bossGroup = supportEpoll ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1);
        workerGroup = supportEpoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        log.info("bossGroup-{}, workerGroup-{}", bossGroup, workerGroup);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(supportEpoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childHandler(channelInitializer)
                // 使用池化ByteBuf
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_REUSEADDR, true)
//                .option(ChannelOption.SO_SNDBUF, 32 * 1024)
//                .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                .option(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        try {
            // 获取绑定地址
            String address = InetAddress.getLocalHost().getHostAddress();
            channelFuture = serverBootstrap.bind(address, getGlobalConfig().getPort());
            // 添加回调通知
            channelFuture.addListener((ChannelFuture future) -> {
                log.info("成功启动服务器，当前服务器类型-{}", this.getClass().getSimpleName());
            });
        } catch (Exception e) {
            log.error("服务器启动过程中发生异常", e);
        }
    }

    @Override
    public void handleRPCRequest(RPCRequest request, ChannelHandlerContext ctx) {
        // TODO: GlobalConfig
        // TODO:RPCTaskRunner
    }

    @Override
    public void close() {
        // TODO: GlobalConfig
    }
}
