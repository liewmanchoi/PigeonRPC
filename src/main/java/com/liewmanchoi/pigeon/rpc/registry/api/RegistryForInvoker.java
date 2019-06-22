package com.liewmanchoi.pigeon.rpc.registry.api;

import com.liewmanchoi.pigeon.rpc.protocol.InvokerService;
import com.liewmanchoi.pigeon.rpc.protocol.ProviderService;

import java.util.List;
import java.util.Map;

/**
 * @author wangsheng
 * @date 2019/6/22
 */
public interface RegistryForInvoker {
    /**
     * 向注册中心注册服务消费者信息
     *
     * @param invokerService 服务消费者信息
     * @date 2019/6/22
     */
    void registerInvoker(final InvokerService invokerService);
    /**
     * 初始化消费者端本地缓存数据结构
     *
     * @date 2019/6/22
     */
    void initProviderMap();
    /**
     * 获取服务提供者信息
     *
     * @return java.util.Map<java.lang.String, java.util.List < com.liewmanchoi.pigeon.rpc.protocol.ProviderService>>
     * @date 2019/6/22
     */
    Map<String, List<ProviderService>> getProviderServiceMap();
}
