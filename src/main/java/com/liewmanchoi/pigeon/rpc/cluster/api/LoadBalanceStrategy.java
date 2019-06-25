package com.liewmanchoi.pigeon.rpc.cluster.api;

import com.liewmanchoi.pigeon.rpc.protocol.ProviderInfo;

import java.util.List;

/**
 * @author wangsheng
 * @date 2019/6/25
 */
public interface LoadBalanceStrategy {
    /**
     * 负载均衡算法
     * @param providerInfoList 服务提供者列表
     * @return ProviderInfo
     */
    ProviderInfo select(List<ProviderInfo> providerInfoList);
}
