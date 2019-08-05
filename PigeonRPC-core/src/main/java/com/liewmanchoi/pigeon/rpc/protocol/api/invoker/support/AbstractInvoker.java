package com.liewmanchoi.pigeon.rpc.protocol.api.invoker.support;

import com.liewmanchoi.pigeon.rpc.config.CommonBean;
import com.liewmanchoi.pigeon.rpc.config.ConsumerBean;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
@Slf4j
public abstract class AbstractInvoker<T> implements Invoker<T> {
  @Setter protected Class<T> interfaceClass;
  protected ConsumerBean<T> consumerBean;
  protected CommonBean commonBean;

  @SuppressWarnings("unchecked")
  public void init(Class<T> interfaceClass) {
    this.interfaceClass = interfaceClass;
    consumerBean = (ConsumerBean<T>) ConsumerBean.getBeanByName(getInterfaceName());
    commonBean = consumerBean.getCommonBean();
  }

  @Override
  public Class<T> getInterface() {
    return interfaceClass;
  }

  @Override
  public String getInterfaceName() {
    return interfaceClass.getName();
  }
}
