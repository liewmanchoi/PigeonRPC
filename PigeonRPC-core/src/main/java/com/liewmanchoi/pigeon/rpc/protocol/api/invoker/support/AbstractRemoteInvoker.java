package com.liewmanchoi.pigeon.rpc.protocol.api.invoker.support;

import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import com.liewmanchoi.pigeon.rpc.transport.api.Client;
import lombok.Getter;
import lombok.Setter;

/**
 * 用于非本地(injvm)调用的Invoker
 *
 * @author wangsheng
 * @date 2019/6/30
 */
public abstract class AbstractRemoteInvoker<T> extends AbstractInvoker<T> {

  /**
   * 底层网络通信设施
   */
  @Getter
  @Setter
  private Client client;

  @Override
  public ServiceURL getServiceURL() {
    return client.getServiceURL();
  }

  @Override
  public boolean isAvailable() {
    return client.isAvailable();
  }
}
