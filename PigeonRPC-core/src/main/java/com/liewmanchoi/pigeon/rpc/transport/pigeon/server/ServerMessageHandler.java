package com.liewmanchoi.pigeon.rpc.transport.pigeon.server;

import com.liewmanchoi.pigeon.rpc.common.domain.Message;
import com.liewmanchoi.pigeon.rpc.transport.api.Server;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/6/29
 */
@Slf4j
@AllArgsConstructor
public class ServerMessageHandler extends ChannelInboundHandlerAdapter {

  private Server server;

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Message message = (Message) msg;
    // 消息类型
    byte type = message.getType();
    log.info("接收到请求-{}", msg);

    // 约定：服务器不会PING客户端
    if (type == Message.PING) {
      log.info("收到客户端PING心跳消息");
      // 响应PONG
      ctx.writeAndFlush(Message.PONG);
    } else if (type == Message.REQUEST) {
      server.handleRPCRequest(message.getRpcRequest(), ctx);
    }

    super.channelRead(ctx, msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    try {
      log.info("消息读取过程中发生异常", cause);
    } finally {
      // 直接关闭连接
      ChannelFuture channelFuture = ctx.close();
      channelFuture.addListener(
          (ChannelFuture future) -> {
            log.info("服务端主动关闭连接");
          });
    }
  }
}
