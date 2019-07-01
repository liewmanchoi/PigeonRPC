package com.liewmanchoi.pigeon.rpc.config;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
public class AbstractConfig {
    // TODO: AbstractConfig类实现


    private GlobalConfig globalConfig;

    public void init(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public RegistryConfig getRegistryConfig() {
        return globalConfig.getRegistryConfig();
    }
}
