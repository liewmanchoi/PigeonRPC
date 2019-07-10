package com.liewmanchoi.pigeon.rpc.transport.pigeon.client;

import com.liewmanchoi.pigeon.rpc.common.domain.Message;
import com.liewmanchoi.pigeon.rpc.transport.api.Client;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息处理器
 *
 * @author wangsheng
 * @date 2019/6/28
 */
@Slf4j
@AllArgsConstructor
public class ClientMessageHandler extends ChannelInboundHandlerAdapter {

  private Client client;

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Message message = (Message) msg;
    log.info("接收到服务器-{} 响应-{}", client.getServiceURL().getAddress(), message);

    // 消息类型
    byte type = message.getType();
    // 约定：服务器不会PING客户端
    if (type == Message.PONG) {
      log.info("收到服务器PONG响应消息");
    } else if (type == Message.RESPONSE) {
      client.handleRPCResponse(message.getRpcResponse());
    }

    super.channelRead(ctx, msg);
  }
}
