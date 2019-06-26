package com.liewmanchoi.pigeon.rpc.cluster.impl;

import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalanceStrategy;
import com.liewmanchoi.pigeon.rpc.protocol.ProviderInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 加权轮询软负载算法
 *
 * @author wangsheng
 * @date 2019/6/25
 */
@Slf4j
public class WeightedPollingStrategy implements LoadBalanceStrategy {
    private int index = 0;
    private Lock lock = new ReentrantLock();

    @Override
    public ProviderInfo select(List<ProviderInfo> providerInfoList) {
        ProviderInfo providerInfo;

        try {
            lock.tryLock(1, TimeUnit.MILLISECONDS);
            List<ProviderInfo> providerList = new ArrayList<>();

            for (ProviderInfo provider : providerInfoList) {
                int weight = provider.getWeight();
                for (int i = 0; i < weight; ++i) {
                    providerList.add(provider);
                }
            }

            if (index >= providerList.size()) {
                index = 0;
            }

            providerInfo = providerList.get(index);
            ++index;

            return providerInfo;
        } catch (InterruptedException e) {
            log.error("ReentrantLock is interrupted.",e);
        } finally {
            lock.unlock();
        }

        return new RandomStrategy().select(providerInfoList);
    }
}
