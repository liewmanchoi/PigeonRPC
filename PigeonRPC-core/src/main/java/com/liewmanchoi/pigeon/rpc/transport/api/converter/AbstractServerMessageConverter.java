package com.liewmanchoi.pigeon.rpc.transport.api.converter;

import com.liewmanchoi.pigeon.rpc.common.domain.Message;

/**
 * @author wangsheng
 * @date 2019/6/29
 */
public abstract class AbstractServerMessageConverter implements MessageConverter {

  public static final AbstractServerMessageConverter DEFAULT_IMPL =
      new AbstractServerMessageConverter() {
        @Override
        protected Object convertMessageToResponse(Message message) {
          return message;
        }

        @Override
        protected Message convertRequestToMessage(Object request) {
          return (Message) request;
        }
      };

  /**
   * convertMessageToResponse
   *
   * @param message 输出对象
   * @return java.lang.Object
   * @date 2019/6/29
   */
  protected abstract Object convertMessageToResponse(Message message);

  /**
   * convertRequestToMessage
   *
   * @param request 收到的请求
   * @return com.liewmanchoi.pigeon.rpc.common.domain.Message
   * @date 2019/6/29
   */
  protected abstract Message convertRequestToMessage(Object request);

  @Override
  public Object convertToObject(Message message) {
    return convertMessageToResponse(message);
  }

  @Override
  public Message convertToMessage(Object object) {
    return convertRequestToMessage(object);
  }
}
