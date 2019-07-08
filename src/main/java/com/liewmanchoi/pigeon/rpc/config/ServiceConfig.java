package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.protocol.api.exporter.Exporter;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 服务配置类
 *
 * @author wangsheng
 * @date 2019/6/27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class ServiceConfig<T> extends AbstractConfig {
  private Class<T> interfaceClass;
  private T reference;
  private Exporter<T> exporter;

  public void export() {
    Invoker<T> invoker =
        getApplicationConfig().getProxyFactoryInstance().getInvoker(reference, interfaceClass);
    exporter = getProtocolConfig().getProtocolInstance().export(invoker, this);
  }
}
