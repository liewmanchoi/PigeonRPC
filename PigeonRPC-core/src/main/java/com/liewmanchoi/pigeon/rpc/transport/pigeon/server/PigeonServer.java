package com.liewmanchoi.pigeon.rpc.transport.pigeon.server;

import com.liewmanchoi.pigeon.rpc.transport.api.constant.FrameConstant;
import com.liewmanchoi.pigeon.rpc.transport.api.converter.AbstractServerMessageConverter;
import com.liewmanchoi.pigeon.rpc.transport.api.converter.MessageConverter;
import com.liewmanchoi.pigeon.rpc.transport.api.support.AbstractServer;
import com.liewmanchoi.pigeon.rpc.transport.pigeon.codec.PigeonEncoder;
import com.liewmanchoi.pigeon.rpc.transport.pigeon.constant.PigeonConstant;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author wangsheng
 * @date 2019/6/29
 */
public class PigeonServer extends AbstractServer {

  @Override
  protected ChannelInitializer initPipeline() {
    return new ChannelInitializer() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            // 心跳检测
            .addLast(
                "IdleStateHandler",
                new IdleStateHandler(
                    PigeonConstant.HEART_BEAT_TIME_OUT_MAX_TIME
                        * PigeonConstant.HEART_BEAT_TIME_OUT,
                    0,
                    0))
            // 出站：添加长度length域
            .addLast(
                "LengthFieldPrepender",
                new LengthFieldPrepender(
                    FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT))
            // 出站：编码器（序列化）
            .addLast("PigeonEncoder", new PigeonEncoder(getGlobalConfig().getSerializer()))
            // 入站：基于帧长度
            .addLast(
                "LengthFieldBasedFrameDecoder",
                new LengthFieldBasedFrameDecoder(
                    FrameConstant.MAX_FRAME_LENGTH,
                    FrameConstant.LENGTH_FIELD_OFFSET,
                    FrameConstant.LENGTH_FIELD_LENGTH,
                    FrameConstant.LENGTH_ADJUSTMENT,
                    FrameConstant.INITIAL_BYTES_TO_STRIP))
            // 入站：心跳检测处理器
            .addLast("ServerHearBeatHandler", new ServerHearBeatHandler())
            // 入站：消息处理器
            .addLast("ServerMessageHandler", new ServerMessageHandler(PigeonServer.this));
      }
    };
  }

  @Override
  protected MessageConverter initMessageConverter() {
    return AbstractServerMessageConverter.DEFAULT_IMPL;
  }
}
