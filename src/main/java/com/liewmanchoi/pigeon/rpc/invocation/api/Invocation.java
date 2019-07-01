package com.liewmanchoi.pigeon.rpc.invocation.api;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequestWrapper;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
public interface Invocation {
    /**
     * Invocation接口
     * @param RPCRequestWrapper request请求
     * @param requestProcessor 请求处理函数
     * @return RPCResponse
     * @throws RPCException 自定义异常
     */
    RPCResponse invoke(RPCRequestWrapper RPCRequestWrapper, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws RPCException;
}
