package com.liewmanchoi.pigeon.rpc.cluster.loadbalancer;

import com.liewmanchoi.pigeon.rpc.cluster.api.support.AbstractLoadBalancer;
import com.liewmanchoi.pigeon.rpc.common.context.InvocationFreqs;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import java.util.List;

/**
 * @author wangsheng
 * @date 2019/7/3
 */
public class LeastActiveLoadBalancer extends AbstractLoadBalancer {

  @Override
  protected Invoker<?> doSelect(List<Invoker> invokers, RPCRequest request) {
    Invoker target = null;
    int freqs = 0;

    for (Invoker invoker : invokers) {
      int currentFreqs =
          InvocationFreqs.getCount(
              invoker.getInterfaceName(),
              request.getMethodName(),
              invoker.getServiceURL().getAddress());

      if (target == null || currentFreqs < freqs) {
        target = invoker;
        freqs = currentFreqs;
      }
    }
    return target;
  }
}
