package com.liewmanchoi.pigeon.rpc.cluster.support;

import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalanceStrategy;
import com.liewmanchoi.pigeon.rpc.cluster.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangsheng
 * @date 2019/6/25
 */
public class LoadBalanceSupport {
    private static final Map<LoadBalanceStrategyType, LoadBalanceStrategy> REGISTRY_MAP = new HashMap<>();

    static {
        REGISTRY_MAP.put(LoadBalanceStrategyType.Random, new RandomStrategy());
        REGISTRY_MAP.put(LoadBalanceStrategyType.Polling, new PollingStrategy());
        REGISTRY_MAP.put(LoadBalanceStrategyType.WeightedRandom, new WeightedRandomStrategy());
        REGISTRY_MAP.put(LoadBalanceStrategyType.WeightedPolling, new WeightedPollingStrategy());
        REGISTRY_MAP.put(LoadBalanceStrategyType.Hash, new HashStrategy());
    }

    public static LoadBalanceStrategy queryLoadBalanceStrategy(String loadBalanceStrategy) {
        LoadBalanceStrategyType loadBalanceStrategyType = LoadBalanceStrategyType.queryByCode(loadBalanceStrategy);
        if (loadBalanceStrategyType == null) {
            return new RandomStrategy();
        }

        return REGISTRY_MAP.get(loadBalanceStrategyType);
    }
}
