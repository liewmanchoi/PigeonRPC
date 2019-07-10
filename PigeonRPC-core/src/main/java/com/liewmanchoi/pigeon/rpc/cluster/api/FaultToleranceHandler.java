package com.liewmanchoi.pigeon.rpc.cluster.api;

import com.liewmanchoi.pigeon.rpc.cluster.ClusterInvoker;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequestWrapper;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;

/**
 * 集群容错接口
 *
 * @author wangsheng
 * @date 2019/7/2
 */
public interface FaultToleranceHandler {
    RPCResponse handle(ClusterInvoker clusterInvoker, RPCRequestWrapper rpcRequestWrapper, RPCException exception);
}
