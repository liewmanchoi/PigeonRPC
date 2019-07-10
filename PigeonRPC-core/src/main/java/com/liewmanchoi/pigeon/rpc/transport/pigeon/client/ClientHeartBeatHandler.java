package com.liewmanchoi.pigeon.rpc.transport.pigeon.client;

import com.liewmanchoi.pigeon.rpc.common.domain.Message;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.transport.api.Client;
import com.liewmanchoi.pigeon.rpc.transport.pigeon.constant.PigeonConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳检测处理器
 *
 * @author wangsheng
 * @date 2019/6/28
 */
@Slf4j
public class ClientHeartBeatHandler extends ChannelInboundHandlerAdapter {
    private int count = 0;
    private Client client;

    public ClientHeartBeatHandler(Client client) {
        this.client = client;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 如果收到服务器发来的消息，则计数器清零
        count = 0;
        // 消息继续向下转发
        ctx.fireChannelRead(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (count < PigeonConstant.HEART_BEAT_TIME_OUT_MAX_TIME) {
                log.warn("客户端在指定之间内没有发送数据，主动发送心跳消息给服务端-{}", client.getServiceURL().getAddress());
                ctx.writeAndFlush(Message.PING);
                // 增加计数器的值
                ++count;
            } else {
                // 超过阈值次数仍然没有收到服务端的响应，调用异常处理方法，开始无限次重连尝试
                client.handleException(new RPCException(ErrorEnum.HEART_BEAT_TIME_OUT_EXCEED, "{} 超过心跳重试次数",ctx.channel()));
            }

            return;
        }

        super.userEventTriggered(ctx, evt);
    }
}
