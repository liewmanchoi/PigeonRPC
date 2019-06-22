package com.liewmanchoi.pigeon.rpc.registry.api;

import com.liewmanchoi.pigeon.rpc.protocol.ProviderService;

import java.util.List;
import java.util.Map;

/**
 * @author wangsheng
 * @date 2019/6/22
 */
public interface RegistryForProvider {
    /**
     * 向注册中心注册服务提供者信息
     * @param providerServices 服务提供者信息
     */
    void registerProvider(final List<ProviderService> providerServices);
    /**
     * 获取服务提供者信息
     *
     * @return java.util.Map<java.lang.String, java.util.List < com.liewmanchoi.pigeon.rpc.protocol.ProviderService>>
     * @date 2019/6/22
     */
    Map<String, List<ProviderService>> getProviderServiceMap();
}
