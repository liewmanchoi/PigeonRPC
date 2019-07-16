package com.liewmanchoi.pigeon.rpc.config.generic;

import com.liewmanchoi.pigeon.rpc.common.ExtensionLoader;
import com.liewmanchoi.pigeon.rpc.common.enumeration.InvokeType;
import com.liewmanchoi.pigeon.rpc.config.ApplicationConfig;
import com.liewmanchoi.pigeon.rpc.config.ClusterConfig;
import com.liewmanchoi.pigeon.rpc.config.GlobalConfig;
import com.liewmanchoi.pigeon.rpc.config.ProtocolConfig;
import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import com.liewmanchoi.pigeon.rpc.config.RegistryConfig;
import com.liewmanchoi.pigeon.rpc.filter.Filter;

/**
 * @author wangsheng
 * @date 2019/7/6
 */
public class GenericServiceBean {
  private ReferenceConfig<?> referenceConfig;

  public void init(
      String interfaceName,
      InvokeType invokeType,
      int timeOut,
      ApplicationConfig applicationConfig,
      ClusterConfig clusterConfig,
      ProtocolConfig protocolConfig,
      RegistryConfig registryConfig) {
    referenceConfig =
        ReferenceConfig.createReferenceConfig(
            interfaceName,
            null,
            invokeType,
            timeOut,
            true,
            ExtensionLoader.getInstance().load(Filter.class));

    referenceConfig.init(
        GlobalConfig.builder()
            .applicationConfig(applicationConfig)
            .protocolConfig(protocolConfig)
            .registryConfig(registryConfig)
            .clusterConfig(clusterConfig)
            .build());
  }

  public Object invoke(String methodName, Class<?>[] argType, Object[] args) {
    return referenceConfig.invokeForGeneric(methodName, argType, args);
  }
}
