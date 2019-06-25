package com.liewmanchoi.pigeon.rpc.cluster.impl;

import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalanceStrategy;
import com.liewmanchoi.pigeon.rpc.protocol.ProviderInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangsheng
 * @date 2019/6/25
 */
public class WeightedRandomStrategy implements LoadBalanceStrategy {
    @Override
    public ProviderInfo select(List<ProviderInfo> providerInfoList) {
        List<ProviderInfo> newList = new ArrayList<>();
        for (ProviderInfo providerInfo : providerInfoList) {
            int weight = providerInfo.getWeight();
            for (int i = 0; i < weight; ++i) {
                newList.add(providerInfo);
            }
        }

        return new RandomStrategy().select(newList);
    }
}
