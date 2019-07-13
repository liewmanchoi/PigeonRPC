package com.liewmanchoi.pigeon.rpc.common.enumeration;

import com.liewmanchoi.pigeon.rpc.common.enumeration.support.ExtensionBaseType;
import com.liewmanchoi.pigeon.rpc.proxy.api.ProxyFactory;
import com.liewmanchoi.pigeon.rpc.proxy.jdk.JDKProxyFactory;

public enum ProxyFactoryType implements ExtensionBaseType<ProxyFactory> {
  /**
   * JDK
   */
  JDK(new JDKProxyFactory());

  private ProxyFactory proxyFactory;

  ProxyFactoryType(ProxyFactory proxyFactory) {
    this.proxyFactory = proxyFactory;
  }

  @Override
  public ProxyFactory getInstance() {
    return proxyFactory;
  }
}
