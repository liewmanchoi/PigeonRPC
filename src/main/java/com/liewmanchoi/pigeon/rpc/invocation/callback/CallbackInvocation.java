package com.liewmanchoi.pigeon.rpc.invocation.callback;

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
public class CallbackInvocation extends AbstractInvocation {
    // TODO: 实现CallbackInvocation

    @Override
    protected <T> RPCResponse doInvoke(RPCRequest rpcRequest, ReferenceConfig<T> referenceConfig, Function<RPCRequest
            , Future<RPCResponse>> requestProcessor) throws Throwable {
        return null;
    }
}
