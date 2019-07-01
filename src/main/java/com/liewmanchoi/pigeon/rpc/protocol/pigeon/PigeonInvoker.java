package com.liewmanchoi.pigeon.rpc.protocol.pigeon;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequestWrapper;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.enumeration.InvokeMode;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.invocation.api.Invocation;
import com.liewmanchoi.pigeon.rpc.invocation.api.InvocationSupport;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.support.AbstractRemoteInvoker;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
public class PigeonInvoker<T> extends AbstractRemoteInvoker<T> {
    @Override
    public RPCResponse invoke(RPCRequestWrapper rpcRequestWrapper) throws RPCException {
        InvokeMode invokeMode = rpcRequestWrapper.getReferenceConfig().getInvokeMode();
        Invocation invocation = InvocationSupport.getInvocation(invokeMode);

        return invocation.invoke(rpcRequestWrapper, (rpcRequest) -> getClient().submit(rpcRequest));
    }
}
