package com.liewmanchoi.pigeon.rpc.serialization.handler;

import com.liewmanchoi.pigeon.rpc.serialization.support.SerializerSupport;
import com.liewmanchoi.pigeon.rpc.serialization.support.SerializerType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author wangsheng
 * @date 2019/6/21
 */
public class NettyEncoderHandler  extends MessageToByteEncoder {
    /**
     * 序列化协议类型
     */
    private SerializerType type;

    public NettyEncoderHandler(SerializerType type) {
        this.type = type;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 将对象序列化为byte数组
        byte[] data = SerializerSupport.serialize(msg, type.getTypeString());
        out.writeBytes(data);
    }
}
