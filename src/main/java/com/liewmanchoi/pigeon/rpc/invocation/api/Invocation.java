package com.liewmanchoi.pigeon.rpc.invocation.api;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.InvokeArgs;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
public interface Invocation {
    RPCResponse invoke(InvokeArgs invokeArgs, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws RPCException;
}
