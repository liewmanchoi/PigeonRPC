package com.liewmanchoi.pigeon.rpc.transport.api.support;

import com.liewmanchoi.pigeon.rpc.common.domain.Message;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.config.CommonBean;
import com.liewmanchoi.pigeon.rpc.config.ProviderBean;
import com.liewmanchoi.pigeon.rpc.executor.api.PigeonExecutor;
import com.liewmanchoi.pigeon.rpc.protocol.api.exporter.Exporter;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.Protocol;
import com.liewmanchoi.pigeon.rpc.transport.api.Server;
import com.liewmanchoi.pigeon.rpc.transport.api.converter.MessageConverter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/6/28
 */
@Slf4j
public abstract class AbstractServer implements Server {
  protected CommonBean commonBean;
  private int port;
  private ChannelInitializer channelInitializer;
  private MessageConverter messageConverter;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private ChannelFuture channelFuture;

  public void init(CommonBean commonBean, int port) {
    this.commonBean = commonBean;
    this.port = port;
    this.channelInitializer = initPipeline();
    this.messageConverter = initMessageConverter();
  }

  /**
   * 初始化ChannelPipeline，设置ChannelHandler(s)
   *
   * @return ChanelInitializer
   */
  protected abstract ChannelInitializer initPipeline();

  /**
   * 初始化消息转换器
   *
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
    serverBootstrap
        .group(bossGroup, workerGroup)
        .channel(supportEpoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
        .childHandler(channelInitializer)
        // 使用池化ByteBuf
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .option(ChannelOption.SO_BACKLOG, 128)
        .option(ChannelOption.SO_REUSEADDR, true)
        .childOption(ChannelOption.TCP_NODELAY, true)
        .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

    try {
      // 获取绑定地址
      channelFuture = serverBootstrap.bind(port);
      // 添加回调通知
      channelFuture.addListener(
          (ChannelFuture future) ->
              log.info(">>>   成功启动服务器，当前服务器类型-{}   <<<", this.getClass().getSimpleName()));
    } catch (Exception e) {
      log.error(">>>   服务器启动过程中发生异常   <<<", e);
    }
  }

  @Override
  public void handleRPCRequest(RPCRequest request, ChannelHandlerContext ctx) {
    Protocol protocol = commonBean.getProtocol();
    PigeonExecutor executor = commonBean.getServerExecutor();

    // 查找已经发布的服务
    ProviderBean<?> providerBean = protocol.referLocalService(request.getInterfaceName());

    // 获取对应的Exporter
    Exporter<?> exporter = providerBean.getExporter();
    // 获取对应的Invoker
    Invoker<?> invoker = exporter.getInvoker();

    // 构造调用任务
    Runnable runnable =
        () -> {
          // 执行方法调用，获得RPCResponse对象
          RPCResponse response = invoker.invoke(request);
          // 发送response
          ctx.channel()
              .writeAndFlush(messageConverter.convertToObject(Message.buildResponse(response)));
        };

    // 提交给线程池执行
    executor.submit(runnable);
  }

  @Override
  public void close() {
    if (workerGroup != null) {
      workerGroup.shutdownGracefully();
    }
    if (bossGroup != null) {
      bossGroup.shutdownGracefully();
    }
    if (channelFuture != null) {
      channelFuture.channel().close();
    }
  }
}
