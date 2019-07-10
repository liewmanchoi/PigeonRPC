package com.liewmanchoi.pigeon.rpc.cluster.loadbalancer;

import com.liewmanchoi.pigeon.rpc.cluster.api.support.AbstractLoadBalancer;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;

/**
 * 带权随机软负载均衡器 <br>
 * 每个Invoker有一个权值weight，作为参数存储在ServiceURL中，值的大小为1 ~ 100 <br>
 * 如果A的权重为1，B的权重为2，则A被选择到的概率为1/3，B为2/3
 *
 * @author wangsheng
 * @date 2019/7/3
 */
@Slf4j
public class WeightedRandomLoadBalancer extends AbstractLoadBalancer {

  @Override
  protected Invoker<?> doSelect(List<Invoker> invokers, RPCRequest request) {
    // 求出所有Invoker的权重之和
    int weightSum =
        invokers.stream()
            .mapToInt(
                invoker ->
                    Integer.parseInt(invoker.getServiceURL().get(ServiceURL.Key.WEIGHT).get(0)))
            .sum();
    // 使用ThreadLocalRandom获取区间[0, weightedSum)之间的随机值
    int randomValue = ThreadLocalRandom.current().nextInt(weightSum);

    for (Invoker invoker : invokers) {
      int currentWeight =
          Integer.parseInt(invoker.getServiceURL().get(ServiceURL.Key.WEIGHT).get(0));
      randomValue -= currentWeight;

      if (randomValue < 0) {
        return invoker;
      }
    }
    return null;
  }
}
