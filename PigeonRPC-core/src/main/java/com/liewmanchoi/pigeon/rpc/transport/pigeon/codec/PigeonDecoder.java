package com.liewmanchoi.pigeon.rpc.transport.pigeon.codec;

import com.liewmanchoi.pigeon.rpc.common.domain.Message;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.serialization.api.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 解码器
 *
 * @author wangsheng
 * @date 2019/6/28
 */
@Slf4j
@AllArgsConstructor
public class PigeonDecoder extends ByteToMessageDecoder {

  private Serializer serializer;

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    // 首先读取type字节
    byte type = in.readByte();

    if (type == Message.PING) {
      // 如果属于PING/PONG消息，则该字节流已经被读完了，直接重建PING/PONG类型的Message即可
      out.add(Message.PING_MSG);
    } else if (type == Message.PONG) {
      out.add(Message.PONG_MSG);
    } else {
      byte[] bytes = new byte[in.readableBytes()];
      in.readBytes(bytes);
      // 如果是RPCRequest/RPCResponse类型的消息，则必须进行反序列化
      if (type == Message.RESPONSE) {
        out.add(Message.buildResponse(serializer.deserialize(bytes, RPCResponse.class)));
      } else if (type == Message.REQUEST) {
        out.add(Message.builderRequest(serializer.deserialize(bytes, RPCRequest.class)));
      }
    }
  }
}
