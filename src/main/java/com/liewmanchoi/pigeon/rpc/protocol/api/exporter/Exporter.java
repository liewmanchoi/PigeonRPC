package com.liewmanchoi.pigeon.rpc.protocol.api.exporter;

import com.liewmanchoi.pigeon.rpc.config.ServiceConfig;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;

/**
 * 进行完服务发布以后的服务提供者，在服务端产生
 *
 * @author wangsheng
 * @date 2019/6/26
 */
public interface Exporter<T> {
    /**
     * getInvoker
     *
     * @return com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker<T>
     * @date 2019/6/26
     */
    Invoker<T> getInvoker();

    /**
     * 获取ServiceConfig对象
     *
     * @return com.liewmanchoi.pigeon.rpc.config.ServiceConfig<T>
     * @date 2019/6/30
     */
    ServiceConfig<T> getServiceConfig();

    /**
     * TODO: 真实含义是什么？
     * 取消暴露服务
     *
     * @date 2019/6/30
     */
    void unexport();
}
