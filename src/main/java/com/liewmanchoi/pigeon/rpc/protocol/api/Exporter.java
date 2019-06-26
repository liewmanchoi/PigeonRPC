package com.liewmanchoi.pigeon.rpc.protocol.api;

/**
 * @author wangsheng
 * @date 2019/6/26
 */
public interface Exporter<T> {
    /**
     * getInvoker
     *
     * @return com.liewmanchoi.pigeon.rpc.protocol.api.Invoker<T>
     * @date 2019/6/26
     */
    Invoker<T> getInvoker();
    // TODO: getServiceConfig
    // TODO: void unexport()
}
