package com.liewmanchoi.pigeon.rpc.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 发送/接收POJO消息对象的抽象
 *
 * 注意事项：
 * Message#type字段直接放入到字节流中，真正序列化和反序列化的对象只有RPCRequest和RPCResponse
 * 好处：PING/PONG只需要发送1个字节，RPCResponse/RPCRequest对象可以使用对象池复用回收
 *
 * @author wangsheng
 * @date 2019/6/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    // 定义消息类型

    public static final byte PING = 1;
    public static final byte PONG = 1 << 1;
    public static final byte REQUEST = 1 << 2;
    public static final byte RESPONSE = 1 << 3;

    public static final Message PING_MSG = new Message(PING);
    public static final Message PONG_MSG = new Message(PONG);

    private byte type;
    private RPCRequest rpcRequest;
    private RPCResponse rpcResponse;

    public Message(byte type) {
        this.type = type;
    }

    public static Message builderRequest(RPCRequest request) {
        return new Message(REQUEST, request, null);
    }

    public static Message buildResponse(RPCResponse response) {
        return new Message(RESPONSE, null, response);
    }
}
