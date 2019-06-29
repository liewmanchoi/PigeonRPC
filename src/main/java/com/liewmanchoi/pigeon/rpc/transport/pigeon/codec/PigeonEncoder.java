package com.liewmanchoi.pigeon.rpc.transport.pigeon.codec;

import com.liewmanchoi.pigeon.rpc.common.domain.Message;
import com.liewmanchoi.pigeon.rpc.serialization.api.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 编码器：Message -> byte[] <br>
 *
 * 注意事项：Message#type字段直接放入到字节流中，真正序列化的对象只有RPCRequest和RPCResponse <br>
 * 好处：PING/PONG只需要发送1个字节，RPCResponse/RPCRequest对象可以使用对象池复用回收 <br>
 *
 * @author wangsheng
 * @date 2019/6/28
 */
@Slf4j
@AllArgsConstructor
public class PigeonEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Message message = (Message) msg;
        // 首先获取消息类型
        byte type = message.getType();
        // 将type字节写入到ByteBuf中
        out.writeByte(type);

        if (type == Message.REQUEST) {
            // 序列化RPCRequest对象
            byte[] requestBytes = serializer.serialize(message.getRpcRequest());
            log.info("RPCRequest类型Message-{}, 序列化大小-{}", message, requestBytes.length);
            // 写入到ByteBuf中
            out.writeBytes(requestBytes);
            // 将RPCRequest对象回收至对象池
            message.getRpcRequest().recycle();
        } else if (type == Message.RESPONSE) {
            byte[] responseBytes = serializer.serialize(message.getRpcResponse());
            log.info("RPCResponse类型Message-{}, 序列化大小-{}", message, responseBytes.length);
            out.writeBytes(responseBytes);
            message.getRpcResponse().recycle();
        }
    }
}
