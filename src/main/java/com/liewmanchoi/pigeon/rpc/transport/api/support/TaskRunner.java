package com.liewmanchoi.pigeon.rpc.transport.api.support;

import com.liewmanchoi.pigeon.rpc.common.domain.Message;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.utils.GlobalRecycler;
import com.liewmanchoi.pigeon.rpc.config.ServiceConfig;
import com.liewmanchoi.pigeon.rpc.transport.api.converter.MessageConverter;
import io.netty.channel.ChannelHandlerContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/7/8
 */
@Slf4j
@AllArgsConstructor
public class TaskRunner implements Runnable {
  private ChannelHandlerContext ctx;
  private RPCRequest request;
  private ServiceConfig<?> serviceConfig;
  private MessageConverter messageConverter;

  @Override
  public void run() {
    RPCResponse response = GlobalRecycler.reuse(RPCResponse.class);
    response.setRequestId(request.getRequestId());

    try {
      Object result = handle(request);
      response.setResult(result);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      response.setCause(e);
      log.error("调用[{}]发生异常[{}]", request, e);
    }

    log.info("调用[{}]的结果为[{}]", request, response);

    // 将response转换为可以接受的协议题（pigeon协议不需要转换）
    Object object = messageConverter.convertToObject(Message.buildResponse(response));
    // 发送
    ctx.writeAndFlush(object);
  }

  private Object handle(RPCRequest request)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Object serviceBean = serviceConfig.getReference();
    Class<?> interfaceClass = serviceBean.getClass();
    String methodName = request.getMethodName();
    Class<?>[] argTypes = request.getArgTypes();
    Object[] args = request.getArgs();

    Method method = interfaceClass.getMethod(methodName, argTypes);
    method.setAccessible(true);
    return method.invoke(serviceBean, args);
  }
}
