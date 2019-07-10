package com.liewmanchoi.pigeon.rpc.registry.api.support;

import com.liewmanchoi.pigeon.rpc.config.RegistryConfig;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceRegistry;
import lombok.Setter;

/**
 * @author wangsheng
 * @date 2019/7/3
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry {
    @Setter
    protected RegistryConfig registryConfig;
}
