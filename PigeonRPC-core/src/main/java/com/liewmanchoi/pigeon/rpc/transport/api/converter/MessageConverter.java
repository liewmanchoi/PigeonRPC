package com.liewmanchoi.pigeon.rpc.transport.api.converter;

import com.liewmanchoi.pigeon.rpc.common.domain.Message;

/**
 * 对象与对象之间的转换器
 *
 * 应用场景：在HTTP协议中，请求对象Message转换为DefaultFullHttpRequest
 *
 * @author wangsheng
 * @date 2019/6/27
 */
public interface MessageConverter {
    /**
     * 将Message对象转换为另一个对象
     * @param message Message
     * @return Object
     */
    Object convertToObject(Message message);

    /**
     * 将Object对象转换为Message对象
     * @param object Object
     * @return Message
     */
    Message convertToMessage(Object object);
}
