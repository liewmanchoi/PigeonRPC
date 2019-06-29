package com.liewmanchoi.pigeon.rpc.transport.pigeon.server;

import com.liewmanchoi.pigeon.rpc.transport.pigeon.constant.PigeonConstant;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;



/**
 * @author wangsheng
 * @date 2019/6/29
 */
@Slf4j
public class ServerHearBeatHandler extends ChannelInboundHandlerAdapter {
    private int timeoutCount = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (timeoutCount < PigeonConstant.HEART_BEAT_TIME_OUT_MAX_TIME) {
                log.warn("在规定时间内没有收到客户端发送的心跳或正常消息");
                ++timeoutCount;
            } else {
                // 直接关闭连接
                ChannelFuture channelFuture = ctx.close();
                channelFuture.addListener((ChannelFuture future) -> {
                    log.info("心跳信息丢失次数超过阈值，服务端主动关闭连接");
                });
            }

            return;
        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 收到客户端发送的消息，计数器清零
        timeoutCount = 0;
        super.channelRead(ctx, msg);
    }
}
