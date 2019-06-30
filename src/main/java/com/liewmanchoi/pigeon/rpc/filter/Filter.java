package com.liewmanchoi.pigeon.rpc.filter;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.protocol.api.InvokeArgs;
import com.liewmanchoi.pigeon.rpc.protocol.api.Invoker;

/**
 * Filter接口
 *
 * @author wangsheng
 * @date 2019/6/30
 */
public interface Filter {
    /**
     * invoke接口
     *
     * @param invoker Invoker
     * @param invokeArgs invokeArgs
     * @return RPCResponse
     * @throws RPCException 异常
     */
    <T> RPCResponse invoke(Invoker<T> invoker, InvokeArgs invokeArgs) throws RPCException;
}
