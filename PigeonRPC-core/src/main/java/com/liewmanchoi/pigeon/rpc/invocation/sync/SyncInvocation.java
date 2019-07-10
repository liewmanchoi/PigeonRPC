package com.liewmanchoi.pigeon.rpc.invocation.sync;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import com.liewmanchoi.pigeon.rpc.invocation.api.support.AbstractInvocation;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/7/1
 */
@Slf4j
public class SyncInvocation extends AbstractInvocation {

  @Override
  protected <T> RPCResponse doInvoke(
      RPCRequest rpcRequest,
      ReferenceConfig<T> referenceConfig,
      Function<RPCRequest, Future<RPCResponse>> requestProcessor)
      throws Throwable {
    Future<RPCResponse> future = requestProcessor.apply(rpcRequest);
    // 同步阻塞
    RPCResponse response = future.get(referenceConfig.getTimeout(), TimeUnit.MILLISECONDS);
    log.info("客户端读取到响应[{}]", response);
    return response;
  }
}
