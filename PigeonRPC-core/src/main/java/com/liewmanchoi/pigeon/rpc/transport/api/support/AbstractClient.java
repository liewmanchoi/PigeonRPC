package com.liewmanchoi.pigeon.rpc.transport.api.support;

import com.liewmanchoi.pigeon.rpc.common.context.RpcSharedContext;
import com.liewmanchoi.pigeon.rpc.common.domain.Message;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.CommonBean;
import com.liewmanchoi.pigeon.rpc.invocation.future.ResponseFuture;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import com.liewmanchoi.pigeon.rpc.transport.api.Client;
import com.liewmanchoi.pigeon.rpc.transport.api.converter.AbstractClientMessageConverter;
import com.liewmanchoi.pigeon.rpc.transport.api.converter.MessageConverter;
import com.liewmanchoi.pigeon.rpc.transport.pigeon.constant.PigeonConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Client抽象基类 各个实现类的不同之处仅仅在于添加的ChannelHandler的不同
 *
 * @author wangsheng
 * @date 2019/6/26
 */
@Slf4j
public abstract class AbstractClient implements Client {
  protected CommonBean commonBean;
  @Getter private ServiceURL serviceURL;
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

  /** 初始化连接（包括建立连接） */
  public void init(CommonBean commonBean, ServiceURL serviceURL) {
    this.commonBean = commonBean;
    this.serviceURL = serviceURL;

    synchronized (AbstractClient.class) {
      if (initialized) {
        return;
      }

      this.messageConverter = initConverter();
      this.eventLoopGroup =
          Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
      this.bootstrap = new Bootstrap();
      this.bootstrap
          .group(eventLoopGroup)
          .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
          .handler(initPipeline())
          .option(ChannelOption.SO_KEEPALIVE, true)
          .option(ChannelOption.TCP_NODELAY, true);

      // 建立连接
      connect();
    }
  }

  /** 建立连接 */
  private synchronized void connect() {
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
      throw new RPCException(
          ErrorEnum.CONNECT_TO_SERVER_FAILURE, "客户端多次重试建立连接的过程中发生错误，放弃重试，并且关闭客户端");
    }
  }

  /**
   * 关闭client
   *
   * <p>要点：必须满足幂等性，保证不会重复关闭endpoint
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

        if (eventLoopGroup != null
            && !eventLoopGroup.isTerminated()
            && !eventLoopGroup.isShutdown()
            && !eventLoopGroup.isShuttingDown()) {
          eventLoopGroup.shutdownGracefully();
        }
      }
    }
  }

  /** 提交并发送RPC调用请求 */
  @Override
  public void submit(RPCRequest request) {
    if (!initialized) {
      connect();
    }

    if (destroyed || closedFromOuter) {
      throw new RPCException(
          ErrorEnum.SUBMIT_AFTER_ENDPOINT_CLOSED, "当前client-{}关闭后仍然在提交任务", serviceURL.getAddress());
    }

    log.info(">>>   客户端发起请求[{}]，请求的服务器[{}]   <<<", request, serviceURL.getAddress());

    // 将request对象封装成Message对象
    Object data = messageConverter.convertToObject(Message.builderRequest(request));
    // 发送message对象
    log.info(">>>   正在发送调用请求[{}]   <<<", serviceURL.getAddress());
    futureChannel.writeAndFlush(data);
  }

  @Override
  public void handleRPCResponse(RPCResponse response) {
    // 将缓存中取出对应的Response future，并设置为相应的值
    ResponseFuture future = RpcSharedContext.getAndRemoveResponseFuture(response.getRequestId());
    if (future != null) {
      future.complete(response);
      return;
    }

    log.warn(">>>   响应[{}]没有对应的ResponseFuture   <<<", response.getRequestId());
  }

  @Override
  public void updateServiceURL(ServiceURL serviceURL) {
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
          final EventLoop loop = futureChannel.eventLoop();
          loop.schedule(connectionRetry, PigeonConstant.HEART_BEAT_TIME_OUT, TimeUnit.SECONDS);
        }
      } else {
        log.info("外部服务器已经关闭，无法进行重连操作...");
      }
    }
  }
}
