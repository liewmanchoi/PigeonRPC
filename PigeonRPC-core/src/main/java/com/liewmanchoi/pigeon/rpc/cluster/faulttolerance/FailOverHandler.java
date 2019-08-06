package com.liewmanchoi.pigeon.rpc.cluster.faulttolerance;

import com.liewmanchoi.pigeon.rpc.cluster.ClusterInvoker;
import com.liewmanchoi.pigeon.rpc.cluster.api.FaultToleranceHandler;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;

/**
 * @author wangsheng
 * @date 2019/7/3
 */
public class FailOverHandler implements FaultToleranceHandler {
  @Override
  public RPCResponse handle(
      ClusterInvoker clusterInvoker, RPCRequest request, RPCException exception) {
    // TODO: handle()
    return null;
  }
}
