package com.liewmanchoi.pigeon.rpc.config;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
public class AbstractConfig {
  private GlobalConfig globalConfig;

  public void init(GlobalConfig globalConfig) {
    this.globalConfig = globalConfig;
  }

  public ApplicationConfig getApplicationConfig() {
    return globalConfig.getApplicationConfig();
  }

  public ClusterConfig getClusterConfig() {
    return globalConfig.getClusterConfig();
  }

  public ProtocolConfig getProtocolConfig() {
    return globalConfig.getProtocolConfig();
  }

  public RegistryConfig getRegistryConfig() {
    return globalConfig.getRegistryConfig();
  }
}
