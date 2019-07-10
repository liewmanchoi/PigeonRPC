package com.liewmanchoi.pigeon.rpc.invocation.api.support;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequestWrapper;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import com.liewmanchoi.pigeon.rpc.invocation.api.Invocation;
import java.util.concurrent.Future;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
@Slf4j
public abstract class AbstractInvocation implements Invocation {

  @Override
  public final RPCResponse invoke(
      RPCRequestWrapper RPCRequestWrapper,
      Function<RPCRequest, Future<RPCResponse>> requestProcessor)
      throws RPCException {
    RPCResponse rpcResponse;
    ReferenceConfig<?> referenceConfig = RPCRequestWrapper.getReferenceConfig();
    RPCRequest rpcRequest = RPCRequestWrapper.getRpcRequest();

    try {
      rpcResponse = doInvoke(rpcRequest, referenceConfig, requestProcessor);
    } catch (Throwable throwable) {
      log.error("调用doInvoke方法过程中发生异常", throwable);
      throw new RPCException(throwable, ErrorEnum.INVOKE_FAILURE, "调用失败");
    }

    return rpcResponse;
  }

  /**
   * 发起调用
   *
   * @param rpcRequest 调用请求
   * @param referenceConfig 引用配置对象
   * @param requestProcessor 调用处理函数体
   * @param <T> 接口类型
   * @return RPCResponse
   * @throws Throwable 所有可能的异常
   */
  protected abstract <T> RPCResponse doInvoke(
      RPCRequest rpcRequest,
      ReferenceConfig<T> referenceConfig,
      Function<RPCRequest, Future<RPCResponse>> requestProcessor)
      throws Throwable;
}
