package com.liewmanchoi.pigeon.rpc.invocation.async;

import com.liewmanchoi.pigeon.rpc.common.context.RPCThreadPrivateContext;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import com.liewmanchoi.pigeon.rpc.invocation.api.support.AbstractInvocation;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author wangsheng
 * @date 2019/7/1
 */
public class AsyncInvocation extends AbstractInvocation {

  @Override
  protected <T> RPCResponse doInvoke(
      RPCRequest rpcRequest,
      ReferenceConfig<T> referenceConfig,
      Function<RPCRequest, Future<RPCResponse>> requestProcessor)
      throws Throwable {
    Future<RPCResponse> future = requestProcessor.apply(rpcRequest);
    // 将future设置到线程私有上下文中
    RPCThreadPrivateContext.getContext().setFuture(future);
    // TODO: 是否需要增加就绪通知
    // 返回空值
    return null;
  }
}
