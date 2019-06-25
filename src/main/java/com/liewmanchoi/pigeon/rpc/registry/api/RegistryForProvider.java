package com.liewmanchoi.pigeon.rpc.registry.api;

import com.liewmanchoi.pigeon.rpc.protocol.ProviderInfo;

/**
 * @author wangsheng
 * @date 2019/6/22
 */
public interface RegistryForProvider {
    /**
     * 向注册中心注册服务提供者信息
     * @param providerInfo 服务提供者信息
     * @exception Exception 异常
     */
    void registerProvider(final ProviderInfo providerInfo) throws Exception;
}
