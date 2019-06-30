package com.liewmanchoi.pigeon.rpc.protocol.api.protocol;

import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import com.liewmanchoi.pigeon.rpc.config.ServiceConfig;
import com.liewmanchoi.pigeon.rpc.protocol.api.exporter.Exporter;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;

/**
 * 协议抽象接口
 *
 * @author wangsheng
 * @date 2019/6/30
 */
public interface Protocol {
    /**
     * 服务发布（服务暴露）
     *
     * @param invoker 待发布的服务所在位置抽象[interface, address]
     * @param serviceConfig 服务配置类ServiceConfig对象
     * @return com.liewmanchoi.pigeon.rpc.protocol.api.exporter.Exporter<T>
     * @date 2019/6/30
     */
    <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException;

    /**
     * 引用服务
     *
     * @param referenceConfig 服务引用配置类ReferenceConfig对象
     * @param serviceURL ServiceURL
     * @return com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker<T>
     * @date 2019/6/30
     */
    <T> Invoker<T> refer(ReferenceConfig<T> referenceConfig, ServiceURL serviceURL) throws RPCException;

    /**
     * 根据接口名查找已经发布的服务
     *
     * @param interfaceName 接口名称
     * @return com.liewmanchoi.pigeon.rpc.config.ServiceConfig<T>
     * @date 2019/6/30
     */
    ServiceConfig<?> referLocalService(String interfaceName) throws RPCException;

    /**
     * 关闭持有的所有连接
     *
     * @date 2019/6/30
     */
    void close();
}
