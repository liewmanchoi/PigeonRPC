package com.liewmanchoi.processor;

import com.liewmanchoi.pigeon.rpc.config.AbstractConfig;
import com.liewmanchoi.pigeon.rpc.config.ApplicationConfig;
import com.liewmanchoi.pigeon.rpc.config.ClusterConfig;
import com.liewmanchoi.pigeon.rpc.config.GlobalConfig;
import com.liewmanchoi.pigeon.rpc.config.ProtocolConfig;
import com.liewmanchoi.pigeon.rpc.config.RegistryConfig;
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

  protected ApplicationContext context;
  private GlobalConfig globalConfig;

  public void init(
      ApplicationConfig applicationConfig,
      ClusterConfig clusterConfig,
      ProtocolConfig protocolConfig,
      RegistryConfig registryConfig) {
    globalConfig =
        GlobalConfig.builder()
            .applicationConfig(applicationConfig)
            .clusterConfig(clusterConfig)
            .protocolConfig(protocolConfig)
            .registryConfig(registryConfig)
            .build();
  }

  void initConfig(AbstractConfig abstractConfig) {
    abstractConfig.init(globalConfig);
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
