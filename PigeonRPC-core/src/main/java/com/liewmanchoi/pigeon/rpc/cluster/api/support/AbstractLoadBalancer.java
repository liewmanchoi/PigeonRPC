package com.liewmanchoi.pigeon.rpc.cluster.api.support;

import com.liewmanchoi.pigeon.rpc.cluster.ClusterInvoker;
import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalancer;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.config.GlobalConfig;
import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/7/2
 */
@Slf4j
public abstract class AbstractLoadBalancer implements LoadBalancer {
  // 负载均衡器位于consumer端，因此持有globalConfig和interface信息缓存

  private GlobalConfig globalConfig;
  private Map<String, ClusterInvoker<?>> clusterInvokerMap = new ConcurrentHashMap<>();

  @Override
  public Invoker<?> select(List<Invoker> invokers, RPCRequest request) {
    if (invokers == null || invokers.isEmpty()) {
      log.warn("select()方法中不存在可供选择的Invoker");
      return null;
    }

    Invoker<?> invoker = doSelect(invokers, request);
    log.info(
        "使用[{}]负载均衡算法确定Request[{}]服务由[{}]提供",
        this.getClass().getSimpleName(),
        request.getRequestId(),
        invoker.getServiceURL());

    return invoker;
  }

  protected abstract Invoker<?> doSelect(List<Invoker> invokers, RPCRequest request);

  @SuppressWarnings("unchecked")
  @Override
  public <T> ClusterInvoker<T> referCluster(ReferenceConfig<T> referenceConfig) {
    String interfaceName = referenceConfig.getInterfaceName();

    if (!clusterInvokerMap.containsKey(interfaceName)) {
      // 如果不包含interfaceName对应的ClusterInvoker，则新建之
      ClusterInvoker<T> clusterInvoker =
          new ClusterInvoker<>(referenceConfig.getInterfaceClass(), globalConfig);
      clusterInvokerMap.put(interfaceName, clusterInvoker);

      return clusterInvoker;
    }

    return (ClusterInvoker<T>) clusterInvokerMap.get(interfaceName);
  }

  public void updateGlobalConfig(GlobalConfig globalConfig) {
    if (this.globalConfig == null) {
      // 如果没有设置globalConfig，则设置之
      this.globalConfig = globalConfig;
      return;
    }

    // 逐项覆盖
    if (globalConfig.getApplicationConfig() != null) {
      this.globalConfig.setApplicationConfig(globalConfig.getApplicationConfig());
    }
    if (globalConfig.getProtocolConfig() != null) {
      this.globalConfig.setProtocolConfig(globalConfig.getProtocolConfig());
    }
    if (globalConfig.getClusterConfig() != null) {
      this.globalConfig.setClusterConfig(globalConfig.getClusterConfig());
    }
    if (globalConfig.getRegistryConfig() != null) {
      this.globalConfig.setRegistryConfig(globalConfig.getRegistryConfig());
    }
  }
}
