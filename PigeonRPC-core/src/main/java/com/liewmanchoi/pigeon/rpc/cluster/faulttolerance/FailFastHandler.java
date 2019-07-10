package com.liewmanchoi.pigeon.rpc.cluster.faulttolerance;

import com.liewmanchoi.pigeon.rpc.cluster.ClusterInvoker;
import com.liewmanchoi.pigeon.rpc.cluster.api.FaultToleranceHandler;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequestWrapper;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/7/3
 */
@Slf4j
public class FailFastHandler implements FaultToleranceHandler {

  @Override
  public RPCResponse handle(
      ClusterInvoker clusterInvoker, RPCRequestWrapper rpcRequestWrapper, RPCException exception) {
    log.error(
        "requestId为[{}]的调用失败，启用FailFast策略，错误为[{}]",
        rpcRequestWrapper.getRpcRequest().getRequestId(),
        exception);
    throw exception;
  }
}
