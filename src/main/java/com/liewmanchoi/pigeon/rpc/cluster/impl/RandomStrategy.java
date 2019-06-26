package com.liewmanchoi.pigeon.rpc.cluster.impl;

import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalanceStrategy;
import com.liewmanchoi.pigeon.rpc.protocol.ProviderInfo;

import java.util.List;
import java.util.Random;

/**
 * 软负载随机算法
 *
 * @author wangsheng
 * @date 2019/6/25
 */
public class RandomStrategy implements LoadBalanceStrategy {
    @Override
    public ProviderInfo select(List<ProviderInfo> providerInfoList) {
        int n = providerInfoList.size();
        int index = new Random().nextInt(n);
        return providerInfoList.get(index);
    }
}
