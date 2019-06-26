package com.liewmanchoi.pigeon.rpc.cluster.impl;

import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalanceStrategy;
import com.liewmanchoi.pigeon.rpc.protocol.ProviderInfo;
import com.liewmanchoi.pigeon.rpc.utils.IpAddressUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author wangsheng
 * @date 2019/6/25
 */
@Slf4j
public class HashStrategy implements LoadBalanceStrategy {
    @Override
    public ProviderInfo select(List<ProviderInfo> providerInfoList) {
        String ipAddress = IpAddressUtils.getIpAddress();
        if (ipAddress != null) {
            int hashCode = ipAddress.hashCode();
            int n = providerInfoList.size();

            return providerInfoList.get(hashCode % n);
        }
        log.warn("IP地址解析失败");
        return new RandomStrategy().select(providerInfoList);
    }
}
