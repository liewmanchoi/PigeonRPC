package com.liewmanchoi.pigeon.rpc.invocation.api;

import com.liewmanchoi.pigeon.rpc.common.context.RpcContext;
import com.liewmanchoi.pigeon.rpc.common.context.RpcSharedContext;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.config.ConsumerBean;
import com.liewmanchoi.pigeon.rpc.invocation.future.ResponseFuture;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
@Slf4j
public class Invocation {
  /**
   * Invocation接口
   *
   * @param request 请求
   * @param consumer 请求处理函数
   * @return RPCResponse
   */
  public static RPCResponse invoke(RPCRequest request, Consumer<RPCRequest> consumer) {
    // 获取interfaceName
    String interfaceName = request.getInterfaceName();
    // 获取对应的ConsumerBean
    ConsumerBean<?> consumerBean = ConsumerBean.getBeanByName(interfaceName);
    // 获取调用方式
    boolean oneway = consumerBean.isOneway();
    boolean async = consumerBean.isAsync();

    ResponseFuture future = null;
    if (!oneway) {
      // 构造ResponseFuture
      future = new ResponseFuture();
      // 缓存结果到RpcSharedContext中，这样才能保证收到response时，向future设置内容
      RpcSharedContext.registerResponseFuture(request.getRequestId(), future);

      if (async) {
        // 如果是异步调用，将future设置到RpcContext中，这样就可以通过RpcContext.getContext().getFuture获取
        RpcContext.getContext().setFuture(future);
      }
    }

    // 执行底层的调用请求发送
    consumer.accept(request);
    if (oneway || async) {
      // 如果属于oneway或异步调用，直接返回null
      return null;
    }
    // 如果是同步调用，则阻塞直至返回结果
    RPCResponse response = null;
    try {
      response = future.get();
    } catch (Exception e) {
      log.error(">>>   同步等待调用请求[{}]的过程中发生了异常   <<<", request.getRequestId(), e);
    }

    return response;
  }
}
