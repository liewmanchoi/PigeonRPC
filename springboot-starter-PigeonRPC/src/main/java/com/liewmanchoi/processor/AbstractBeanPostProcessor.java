package com.liewmanchoi.processor;

import com.liewmanchoi.pigeon.rpc.config.AbstractBean;
import com.liewmanchoi.pigeon.rpc.config.CommonBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author wangsheng
 * @date 2019/7/11
 */
public abstract class AbstractBeanPostProcessor
    implements BeanPostProcessor, ApplicationContextAware {
  private CommonBean commonBean;
  private ApplicationContext context;

  public void init(CommonBean commonBean) {
    this.commonBean = commonBean;
  }

  void initCommon(AbstractBean abstractBean) {
    abstractBean.setCommonBean(commonBean);
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.context = applicationContext;
  }
}
