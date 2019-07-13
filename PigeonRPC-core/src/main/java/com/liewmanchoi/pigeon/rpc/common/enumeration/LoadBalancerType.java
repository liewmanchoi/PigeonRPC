package com.liewmanchoi.pigeon.rpc.common.enumeration;

import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalancer;
import com.liewmanchoi.pigeon.rpc.cluster.loadbalancer.ConsistentHashLoadBalancer;
import com.liewmanchoi.pigeon.rpc.cluster.loadbalancer.LeastActiveLoadBalancer;
import com.liewmanchoi.pigeon.rpc.cluster.loadbalancer.RandomLoadBalancer;
import com.liewmanchoi.pigeon.rpc.cluster.loadbalancer.RoundRobinLoadBalancer;
import com.liewmanchoi.pigeon.rpc.cluster.loadbalancer.WeightedRandomLoadBalancer;
import com.liewmanchoi.pigeon.rpc.common.enumeration.support.ExtensionBaseType;

/**
 * @author wangsheng
 * @date 2019/7/13
 */
public enum LoadBalancerType implements ExtensionBaseType<LoadBalancer> {
  /**
   * LeastActive
   */
  LEAST_ACTIVE(new LeastActiveLoadBalancer()),
  /**
   * Random
   */
  RANDOM(new RandomLoadBalancer()),
  /**
   * RoundRobin
   */
  ROUND_ROBIN(new RoundRobinLoadBalancer()),
  /**
   * WeightedRandom
   */
  WEIGHTED_RANDOM(new WeightedRandomLoadBalancer()),
  /**
   * ConsistentHash
   */
  CONSISTENT_HASH(new ConsistentHashLoadBalancer());

  private LoadBalancer loadBalancer;

  LoadBalancerType(LoadBalancer loadBalancer) {
    this.loadBalancer = loadBalancer;
  }

  @Override
  public LoadBalancer getInstance() {
    return loadBalancer;
  }
}
