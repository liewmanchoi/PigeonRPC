package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.protocol.api.exporter.Exporter;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/8/5
 */
@Slf4j
@Getter
@Setter
public class ProviderBean<T> extends AbstractBean {
  private Class<T> interfaceClass;
  /** 服务提供者对象 */
  private T bean;

  private Exporter<T> exporter;

  public static <H> ProviderBean<H> createBean(Class<H> interfaceClass, H bean) {
    ProviderBean<H> providerBean = new ProviderBean<>();
    providerBean.setInterfaceClass(interfaceClass);
    providerBean.setBean(bean);

    return providerBean;
  }

  /** 服务暴露，允许对外提供服务 */
  public void export() {
    Invoker<T> invoker = commonBean.getProxyFactory().getInvoker(bean, interfaceClass);
    exporter = commonBean.getProtocol().export(invoker, this);
  }
}
