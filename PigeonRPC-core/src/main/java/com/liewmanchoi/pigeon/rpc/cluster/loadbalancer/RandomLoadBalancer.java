package com.liewmanchoi.pigeon.rpc.cluster.loadbalancer;

import com.liewmanchoi.pigeon.rpc.cluster.api.support.AbstractLoadBalancer;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author wangsheng
 * @date 2019/7/3
 */
public class RandomLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected Invoker<?> doSelect(List<Invoker> invokers, RPCRequest request) {
        return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));
    }
}
