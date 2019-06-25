package com.liewmanchoi.pigeon.rpc.cluster.support;

import lombok.Getter;

/**
 * @author wangsheng
 */
public enum LoadBalanceStrategyType {
    /**
     * 随机算法
     */
    Random("Random"),
    /**
     * 轮询算法
     */
    Polling("Polling"),
    /**
     * 加权随机算法
     */
    WeightedRandom("WeightedRandom"),
    /**
     * 加权轮询算法
     */
    WeightedPolling("WeightedPolling"),
    /**
     * 哈希算法
     */
    Hash("Hash");


    /**
     * 软负载算法类型
     */
    private @Getter String code;

    LoadBalanceStrategyType(String code) {
        this.code = code;
    }

    public static LoadBalanceStrategyType queryByCode(String code) {
        if (code == null || code.equals("")) {
            return null;
        }

        for (LoadBalanceStrategyType strategy : values()) {
            if (code.equals(strategy.getCode())) {
                return strategy;
            }
        }

        return null;
    }
}
