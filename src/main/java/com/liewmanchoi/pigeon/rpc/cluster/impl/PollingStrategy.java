package com.liewmanchoi.pigeon.rpc.cluster.impl;

import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalanceStrategy;
import com.liewmanchoi.pigeon.rpc.protocol.ProviderInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 软负载轮询算法
 *
 * @author wangsheng
 * @date 2019/6/25
 */
@Slf4j
public class PollingStrategy implements LoadBalanceStrategy {
    private int index = 0;
    private Lock lock = new ReentrantLock();

    @Override
    public ProviderInfo select(List<ProviderInfo> providerInfoList) {
        ProviderInfo providerInfo = null;

        try {
            lock.tryLock(1, TimeUnit.MILLISECONDS);
            if (index >= providerInfoList.size()) {
                index = 0;
            }

            providerInfo = providerInfoList.get(index);
            ++index;
        } catch (InterruptedException e) {
            log.error("ReentrantLock is interrupted.",e);
        } finally {
            lock.unlock();
        }

        if (providerInfo == null) {
            providerInfo = new RandomStrategy().select(providerInfoList);
        }

        return providerInfo;
    }
}
