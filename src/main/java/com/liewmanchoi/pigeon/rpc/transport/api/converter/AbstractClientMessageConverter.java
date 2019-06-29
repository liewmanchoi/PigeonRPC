package com.liewmanchoi.pigeon.rpc.transport.api.converter;

import com.liewmanchoi.pigeon.rpc.common.domain.Message;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;

/**
 * @author wangsheng
 * @date 2019/6/27
 */
public abstract class AbstractClientMessageConverter implements MessageConverter {
    /**
     * convertMessageToRequest
     *
     * @param message Message
     * @return java.lang.Object
     * @date 2019/6/27
     */
    public abstract Object convertMessageToRequest(Message message);
    /**
     * convertResponseToMessage
     *
     * @param response Object
     * @return java.lang.Object
     * @date 2019/6/27
     */
    public abstract Message convertResponseToMessage(Object response);

    public static final AbstractClientMessageConverter DEFAULT_IMPL = new AbstractClientMessageConverter() {
        @Override
        public Object convertMessageToRequest(Message message) {
            return message;
        }

        @Override
        public Message convertResponseToMessage(Object response) {
            return (Message) response;
        }
    };

    @Override
    public Object convertToObject(Message message) {
        return convertMessageToRequest(message);
    }

    @Override
    public Message convertToMessage(Object object) {
        return convertResponseToMessage(object);
    }
}
