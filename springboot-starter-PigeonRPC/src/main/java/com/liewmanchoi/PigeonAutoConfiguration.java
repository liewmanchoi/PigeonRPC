package com.liewmanchoi;

import com.liewmanchoi.pigeon.rpc.cluster.api.FaultToleranceHandler;
import com.liewmanchoi.pigeon.rpc.cluster.api.support.AbstractLoadBalancer;
import com.liewmanchoi.pigeon.rpc.common.ExtensionLoader;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ExecutorType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.FaultToleranceType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.LoadBalancerType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ProtocolType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ProxyFactoryType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.SerializerType;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.ApplicationConfig;
import com.liewmanchoi.pigeon.rpc.config.ClusterConfig;
import com.liewmanchoi.pigeon.rpc.config.ExecutorConfig;
import com.liewmanchoi.pigeon.rpc.config.GlobalConfig;
import com.liewmanchoi.pigeon.rpc.config.ProtocolConfig;
import com.liewmanchoi.pigeon.rpc.config.RegistryConfig;
import com.liewmanchoi.pigeon.rpc.executor.api.PigeonExecutor;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.support.AbstractProtocol;
import com.liewmanchoi.pigeon.rpc.proxy.api.ProxyFactory;
import com.liewmanchoi.pigeon.rpc.registry.zookeeper.ZookeeperServiceRegistry;
import com.liewmanchoi.pigeon.rpc.serialization.api.Serializer;
import com.liewmanchoi.processor.ConsumerBeanProcessor;
import com.liewmanchoi.processor.ProviderBeanPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangsheng
 * @date 2019/7/12
 */
@EnableConfigurationProperties(value = PigeonProperties.class)
@Configuration
@Slf4j
public class PigeonAutoConfiguration implements InitializingBean {

  @Autowired
  private PigeonProperties pigeonProperties;
  private ExtensionLoader extensionLoader;

  @Bean(initMethod = "init", destroyMethod = "close")
  public RegistryConfig registryConfig() {
    RegistryConfig registryConfig = pigeonProperties.getRegistryConfig();

    if (registryConfig == null) {
      log.error("没有配置RegistryConfig");
      throw new RPCException(ErrorEnum.APP_CONFIG_FILE_ERROR, "没有配置RegistryConfig");
    }

    ZookeeperServiceRegistry registryInstance = new ZookeeperServiceRegistry(registryConfig);
    registryConfig.setRegistryInstance(registryInstance);

    return registryConfig;
  }

  @Bean
  public ApplicationConfig applicationConfig() {
    ApplicationConfig applicationConfig = pigeonProperties.getApplicationConfig();
    if (applicationConfig == null) {
      log.error("没有配置ApplicationConfig");
      throw new RPCException(ErrorEnum.APP_CONFIG_FILE_ERROR, "没有配置ApplicationConfig");
    }
    applicationConfig.setProxyFactoryInstance(
        extensionLoader.load(
            ProxyFactory.class, ProxyFactoryType.class, applicationConfig.getProxy()));
    applicationConfig.setSerializerInstance(
        extensionLoader.load(
            Serializer.class, SerializerType.class, applicationConfig.getSerialize()));

    return applicationConfig;
  }

  @Bean
  public ClusterConfig clusterConfig(
      RegistryConfig registryConfig, ApplicationConfig applicationConfig) {
    ClusterConfig clusterConfig = pigeonProperties.getClusterConfig();
    if (clusterConfig == null) {
      log.error("没有配置ClusterConfig");
      throw new RPCException(ErrorEnum.APP_CONFIG_FILE_ERROR, "没有配置ClusterConfig");
    }

    AbstractLoadBalancer loadBalancer =
        extensionLoader.load(
            AbstractLoadBalancer.class, LoadBalancerType.class, clusterConfig.getLoadBalancer());

    loadBalancer.updateGlobalConfig(
        GlobalConfig.builder()
            .applicationConfig(applicationConfig)
            .registryConfig(registryConfig)
            .clusterConfig(clusterConfig)
            .build());
    if (clusterConfig.getFaultTolerance() != null) {
      clusterConfig.setFaultToleranceHandlerInstance(
          extensionLoader.load(
              FaultToleranceHandler.class,
              FaultToleranceType.class,
              clusterConfig.getFaultTolerance()));
    } else {
      clusterConfig.setFaultToleranceHandlerInstance(FaultToleranceType.FAIL_OVER.getInstance());
    }

    clusterConfig.setLoadBalancerInstance(loadBalancer);

    // TODO: LeastActive策略

    return clusterConfig;
  }

  @Bean(destroyMethod = "close")
  public ProtocolConfig protocolConfig(
      ApplicationConfig applicationConfig,
      RegistryConfig registryConfig,
      ClusterConfig clusterConfig) {
    ProtocolConfig protocolConfig = pigeonProperties.getProtocolConfig();

    if (protocolConfig == null) {
      log.error("没有配置ProtocolConfig");
      throw new RPCException(ErrorEnum.APP_CONFIG_FILE_ERROR, "没有配置ProtocolConfig");
    }

    AbstractProtocol protocol = extensionLoader
        .load(AbstractProtocol.class, ProtocolType.class, protocolConfig.getType());
    protocol.init(GlobalConfig.builder()
        .applicationConfig(applicationConfig)
        .clusterConfig(clusterConfig)
        .registryConfig(registryConfig)
        .protocolConfig(protocolConfig)
        .build());
    protocolConfig.setProtocolInstance(protocol);

    ((AbstractLoadBalancer) clusterConfig.getLoadBalancerInstance()).updateGlobalConfig(
        GlobalConfig.builder()
            .protocolConfig(protocolConfig).build());

    ExecutorConfig serverConfig = protocolConfig.getServerConfig();
    if (serverConfig != null) {
      PigeonExecutor executor = extensionLoader
          .load(PigeonExecutor.class, ExecutorType.class, serverConfig.getType());
      executor.init(serverConfig.getThreads());
      serverConfig.setExecutorInstance(executor);
    }

    ExecutorConfig clientConfig = protocolConfig.getClientConfig();
    if (clientConfig != null) {
      PigeonExecutor executor =
          extensionLoader.load(PigeonExecutor.class, ExecutorType.class, clientConfig.getType());
      executor.init(clientConfig.getThreads());
      clientConfig.setExecutorInstance(executor);
    }

    return protocolConfig;
  }

  @Bean
  public ConsumerBeanProcessor consumerBeanProcessor(
      ApplicationConfig applicationConfig,
      ClusterConfig clusterConfig,
      ProtocolConfig protocolConfig,
      RegistryConfig registryConfig) {
    ConsumerBeanProcessor consumerBeanProcessor = new ConsumerBeanProcessor();
    consumerBeanProcessor.init(applicationConfig, clusterConfig, protocolConfig, registryConfig);
    log.info("ConsumerBeanProcessor已经完成初始化");
    return consumerBeanProcessor;
  }

  @Bean
  public ProviderBeanPostProcessor providerBeanPostProcessor(
      ApplicationConfig applicationConfig,
      ClusterConfig clusterConfig,
      ProtocolConfig protocolConfig,
      RegistryConfig registryConfig) {
    ProviderBeanPostProcessor processor = new ProviderBeanPostProcessor();
    processor.init(applicationConfig, clusterConfig, protocolConfig, registryConfig);
    log.info("ProviderBeanProcessor已经完成初始化");

    return processor;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    extensionLoader = ExtensionLoader.getInstance();
    extensionLoader.loadResources();
  }
}
