package com.liewmanchoi;

import com.liewmanchoi.pigeon.rpc.common.enumeration.ExecutorType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ProtocolType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ProxyFactoryType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.RegistryType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.SerializerType;
import com.liewmanchoi.pigeon.rpc.config.CommonBean;
import com.liewmanchoi.pigeon.rpc.executor.api.PigeonExecutor;
import com.liewmanchoi.pigeon.rpc.executor.pigeon.PigeonExecutorService;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.Protocol;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.support.AbstractProtocol;
import com.liewmanchoi.pigeon.rpc.protocol.pigeon.PigeonProtocol;
import com.liewmanchoi.pigeon.rpc.proxy.api.ProxyFactory;
import com.liewmanchoi.pigeon.rpc.proxy.jdk.JDKProxyFactory;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceRegistry;
import com.liewmanchoi.pigeon.rpc.registry.zookeeper.ZookeeperServiceRegistry;
import com.liewmanchoi.pigeon.rpc.serialization.api.Serializer;
import com.liewmanchoi.pigeon.rpc.serialization.protostuff.ProtoStuffSerializer;
import com.liewmanchoi.processor.ConsumerBeanProcessor;
import com.liewmanchoi.processor.ProviderBeanPostProcessor;
import com.liewmanchoi.properties.PigeonProperties;
import com.liewmanchoi.properties.ProtocolConfig;
import com.liewmanchoi.properties.RegistryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangsheng
 * @date 2019/7/12
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = PigeonProperties.class)
public class PigeonAutoConfiguration {
  @Autowired private PigeonProperties pigeonProperties;

  /** 配置服务注册ServiceRegistry */
  private ServiceRegistry serviceRegistry() {
    RegistryConfig registryConfig = pigeonProperties.getRegistry();
    if (registryConfig == null) {
      throw new RuntimeException(">>>   没有配置服务注册项   <<<");
    }

    ServiceRegistry serviceRegistry;
    if (registryConfig.getProtocol() != null) {
      serviceRegistry = RegistryType.valueOf(registryConfig.getProtocol()).getInstance();
    } else {
      serviceRegistry = new ZookeeperServiceRegistry();
    }

    String address = registryConfig.getAddress();
    if (address == null || address.isEmpty()) {
      log.error(">>>   必须配置服务发现地址   <<<");
      throw new RuntimeException("没有配置服务发现地址");
    }

    serviceRegistry.init(address);
    log.info(">>>   ServiceRegistry初始化完毕   <<<");
    return serviceRegistry;
  }

  /** 配置动态代理ProxyFactory */
  private ProxyFactory proxyFactory() {
    ProtocolConfig protocolConfig = pigeonProperties.getProtocol();
    if (protocolConfig == null || protocolConfig.getProxy() == null) {
      log.info(">>>   没有设置proxy选项   <<<");
      return new JDKProxyFactory();
    }

    return ProxyFactoryType.valueOf(protocolConfig.getProxy()).getInstance();
  }

  /** 配置序列化器 */
  private Serializer serializer() {
    ProtocolConfig protocolConfig = pigeonProperties.getProtocol();
    if (protocolConfig == null || protocolConfig.getSerializer() == null) {
      log.info(">>>   没有设置serializer选项  <<<");
      return new ProtoStuffSerializer();
    }

    return SerializerType.valueOf(protocolConfig.getSerializer()).getInstance();
  }

  /** 配置线程池 */
  private PigeonExecutor executor() {
    ProtocolConfig protocolConfig = pigeonProperties.getProtocol();

    PigeonExecutor executor;
    if (protocolConfig == null || protocolConfig.getExecutor() == null) {
      log.info(">>>   没有设置线程池选项   <<<");
      executor = new PigeonExecutorService();
    } else {
      executor = ExecutorType.valueOf(protocolConfig.getExecutor()).getInstance();
    }

    if (protocolConfig != null && protocolConfig.getThreads() != null) {
      log.info(">>>   设置的线程数量为[{}]   <<<", protocolConfig.getThreads());
      executor.init(protocolConfig.getThreads());
    } else {
      executor.init(Runtime.getRuntime().availableProcessors());
    }

    return executor;
  }

  @Bean
  public CommonBean commonBean() {
    CommonBean commonBean = new CommonBean();
    commonBean.setServiceRegistry(serviceRegistry());
    commonBean.setProxyFactory(proxyFactory());
    commonBean.setSerializer(serializer());
    commonBean.setServerExecutor(executor());

    return commonBean;
  }

  /** 配置Protocol */
  @Bean(destroyMethod = "destroy")
  public Protocol protocol() {
    ProtocolConfig protocolConfig = pigeonProperties.getProtocol();
    Protocol protocol;
    if (protocolConfig == null || protocolConfig.getProtocol() == null) {
      protocol = new PigeonProtocol();
    } else {
      protocol = ProtocolType.valueOf(protocolConfig.getProtocol()).getInstance();
    }

    if (protocolConfig == null || protocolConfig.getPort() == null) {
      log.error(">>>   必须配置端口号   <<<");
      throw new RuntimeException("没有配置服务端口号");
    }
    // 初始化端口号
    ((AbstractProtocol) protocol).init(commonBean(), protocolConfig.getPort());
    // 设置protocol
    commonBean().setProtocol(protocol);

    log.info(">>>   protocol初始化完毕   <<<");
    return protocol;
  }

  @Bean
  public ConsumerBeanProcessor consumerBeanProcessor() {
    ConsumerBeanProcessor consumerBeanProcessor = new ConsumerBeanProcessor();
    consumerBeanProcessor.init(commonBean());
    log.info(">>>   ConsumerBeanProcessor初始化完成   <<<");
    return consumerBeanProcessor;
  }

  @Bean
  public ProviderBeanPostProcessor providerBeanPostProcessor() {
    ProviderBeanPostProcessor providerBeanPostProcessor = new ProviderBeanPostProcessor();
    providerBeanPostProcessor.init(commonBean());
    log.info(">>>   ProviderBeanProcessor已经完成初始化   <<<");

    return providerBeanPostProcessor;
  }
}
