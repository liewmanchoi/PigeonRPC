package com.liewmanchoi.pigeon.rpc.serialization.handler;

import com.liewmanchoi.pigeon.rpc.serialization.support.SerializerSupport;
import com.liewmanchoi.pigeon.rpc.serialization.support.SerializerType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author wangsheng
 * @date 2019/6/21
 */
public class NettyDecoderHandler extends ByteToMessageDecoder {
    /**
     * Class类对象
     */
    private Class<?> clazz;
    /**
     * 序列化协议类型
     */
    private SerializerType type;

    public NettyDecoderHandler(Class<?> clazz, SerializerType type) {
        this.clazz = clazz;
        this.type = type;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object object = SerializerSupport.deserialize(in.array(), clazz, type.getTypeString());
        out.add(object);
    }
}
