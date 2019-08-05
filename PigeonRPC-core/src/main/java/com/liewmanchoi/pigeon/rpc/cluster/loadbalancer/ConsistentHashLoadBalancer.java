package com.liewmanchoi.pigeon.rpc.cluster.loadbalancer;

import com.liewmanchoi.pigeon.rpc.cluster.ClusterInvoker;
import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalancer;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.config.ConsumerBean;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import java.util.List;

/**
 * @author wangsheng
 * @date 2019/7/3
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

  @Override
  public Invoker<?> select(List<Invoker> invokers, RPCRequest request) {
    return null;
  }

  @Override
  public <T> ClusterInvoker<T> referCluster(ConsumerBean<T> consumerBean) {
    return null;
  }
  // TODO: 一致性哈希算法的实现
}
