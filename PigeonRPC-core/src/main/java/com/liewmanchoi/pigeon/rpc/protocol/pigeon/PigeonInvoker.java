package com.liewmanchoi.pigeon.rpc.protocol.pigeon;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.invocation.api.Invocation;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.support.AbstractRemoteInvoker;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
public class PigeonInvoker<T> extends AbstractRemoteInvoker<T> {
  @Override
  public RPCResponse invoke(RPCRequest request) throws RPCException {
    return Invocation.invoke(request, (rpcRequest -> client.submit(rpcRequest)));
  }
}
