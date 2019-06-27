package com.liewmanchoi.pigeon.rpc.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 发送/接收POJO消息对象的抽象
 *
 * @author wangsheng
 * @date 2019/6/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    // 定义消息类型

    public static final Byte PING = 1;
    public static final Byte PONG = 1 << 1;
    public static final Byte REQUEST = 1 << 2;
    public static final Byte RESPONSE = 1 << 3;

    public static final Message PING_MSG = new Message(PING);
    public static final Message PONG_MSG = new Message(PONG);

    private Byte type;
    private RPCRequest rpcRequest;
    private RPCResponse rpcResponse;

    public Message(Byte type) {
        this.type = type;
    }

    public static Message builderRequest(RPCRequest request) {
        return new Message(REQUEST, request, null);
    }

    public static Message buildResponse(RPCResponse response) {
        return new Message(RESPONSE, null, response);
    }
}
