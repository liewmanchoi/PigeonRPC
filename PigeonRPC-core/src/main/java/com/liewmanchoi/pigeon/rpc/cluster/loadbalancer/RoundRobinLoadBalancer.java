package com.liewmanchoi.pigeon.rpc.cluster.loadbalancer;

import com.liewmanchoi.pigeon.rpc.cluster.api.support.AbstractLoadBalancer;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询调度算法Round-Robin
 *
 * @author wangsheng
 * @date 2019/7/3
 */
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {

  private AtomicInteger atomicInteger = new AtomicInteger(0);

  @Override
  protected Invoker<?> doSelect(List<Invoker> invokers, RPCRequest request) {
    return invokers.get(atomicInteger.getAndUpdate(i -> (i + 1) % invokers.size()));
  }
}
