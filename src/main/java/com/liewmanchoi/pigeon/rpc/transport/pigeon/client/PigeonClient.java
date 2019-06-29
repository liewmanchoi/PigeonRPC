package com.liewmanchoi.pigeon.rpc.transport.pigeon.client;

import com.liewmanchoi.pigeon.rpc.transport.api.constant.FrameConstant;
import com.liewmanchoi.pigeon.rpc.transport.api.converter.AbstractClientMessageConverter;
import com.liewmanchoi.pigeon.rpc.transport.api.support.AbstractClient;
import com.liewmanchoi.pigeon.rpc.transport.pigeon.codec.PigeonDecoder;
import com.liewmanchoi.pigeon.rpc.transport.pigeon.codec.PigeonEncoder;
import com.liewmanchoi.pigeon.rpc.transport.pigeon.constant.PigeonConstant;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/6/28
 */
@Slf4j
public class PigeonClient extends AbstractClient {
    @Override
    protected ChannelInitializer initPipeline() {
        log.info("PigeonClient init pipeline...");
        return new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline()
                // 出站编码器
                .addLast("IdleStateHandler", new IdleStateHandler(0, PigeonConstant.HEART_BEAT_TIME_OUT, 0))
                        // 添加length域解决半包/拆包问题
                .addLast("LengthFieldPrepender", new LengthFieldPrepender(FrameConstant.LENGTH_FIELD_LENGTH,
                        FrameConstant.LENGTH_ADJUSTMENT))
                .addLast("PigeonEncoder", new PigeonEncoder(getGlobalConfig().getSerializer()))
                // 入站解码器
                .addLast("LengthFieldBasedFrameDecoder",
                        new LengthFieldBasedFrameDecoder(FrameConstant.MAX_FRAME_LENGTH,
                                FrameConstant.LENGTH_FIELD_OFFSET, FrameConstant.LENGTH_FIELD_LENGTH,
                                FrameConstant.LENGTH_ADJUSTMENT, FrameConstant.INITIAL_BYTES_TO_STRIP))
                        // ByteBuf -> Message
                .addLast("PigeonDecoder", new PigeonDecoder(getGlobalConfig().getSerializer()))
                // 心跳检测
                .addLast("ClientHearBeatHandler", new ClientHeartBeatHandler(PigeonClient.this))
                // 处理消息
                .addLast("ClientMessageHandler", new ClientMessageHandler(PigeonClient.this));
            }
        };
    }

    @Override
    protected AbstractClientMessageConverter initConverter() {
        // 对于自定义Pigeon协议来说，实质上没有进行转换
        return AbstractClientMessageConverter.DEFAULT_IMPL;
    }
}
