package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.cluster.api.FaultToleranceHandler;
import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalancer;
import com.liewmanchoi.pigeon.rpc.common.enumeration.FaultToleranceType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.LoadBalancerType;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/8/4
 */
@Slf4j
@Getter
@Setter
public class ConsumerBean<T> extends AbstractBean {
  private static final Map<String, ConsumerBean<?>> CONSUMER_BEAN_MAP = new ConcurrentHashMap<>();
  private String interfaceName;
  private Class<T> interfaceClass;
  private boolean oneway;
  private boolean async;
  /** 保证多线程可见性 */
  private volatile T proxyBean;

  private volatile Invoker<T> invoker;
  private LoadBalancer loadBalancer;
  private FaultToleranceHandler faultToleranceHandler;
  private volatile boolean initialized;

  private ConsumerBean() {}

  @SuppressWarnings("unchecked")
  public static <H> ConsumerBean<H> createConsumerBean(
      Class<H> interfaceClass,
      boolean oneway,
      boolean async,
      LoadBalancerType loadBalancerType,
      FaultToleranceType faultToleranceType) {
    String interfaceName = interfaceClass.getName();
    if (CONSUMER_BEAN_MAP.containsKey(interfaceName)) {
      return (ConsumerBean<H>) CONSUMER_BEAN_MAP.get(interfaceName);
    }

    ConsumerBean<H> consumerBean = new ConsumerBean<>();
    consumerBean.setInterfaceClass(interfaceClass);
    consumerBean.setInterfaceName(interfaceName);
    consumerBean.setOneway(oneway);
    consumerBean.setAsync(async);
    consumerBean.setLoadBalancer(loadBalancerType.getInstance());
    consumerBean.setFaultToleranceHandler(faultToleranceType.getInstance());

    return consumerBean;
  }

  public static ConsumerBean<?> getBeanByName(String name) {
    return CONSUMER_BEAN_MAP.get(name);
  }

  private synchronized void init() {
    if (initialized) {
      return;
    }
    initialized = true;
    // 生成ClusterInvoker
    invoker = loadBalancer.referCluster(this);
    // 生成代理类对象
    proxyBean = commonBean.getProxyFactory().createProxy(invoker);
  }

  public T getProxyBean() {
    if (!initialized) {
      init();
    }

    return proxyBean;
  }
}
