package com.liewmanchoi.pigeon.rpc.common.enumeration;

import com.liewmanchoi.pigeon.rpc.common.enumeration.support.ExtensionBaseType;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceRegistry;
import com.liewmanchoi.pigeon.rpc.registry.zookeeper.ZookeeperServiceRegistry;

/**
 * @author wangsheng
 * @date 2019/8/4
 */
public enum RegistryType implements ExtensionBaseType<ServiceRegistry> {
  /** Zookeeper注册中心 */
  ZOOKEEPER(new ZookeeperServiceRegistry());

  private ServiceRegistry serviceRegistry;

  RegistryType(ServiceRegistry serviceRegistry) {
    this.serviceRegistry = serviceRegistry;
  }

  @Override
  public ServiceRegistry getInstance() {
    return serviceRegistry;
  }
}
