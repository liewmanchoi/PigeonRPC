package com.liewmanchoi.pigeon.rpc.registry.api;

import com.liewmanchoi.pigeon.rpc.protocol.InvokerService;
import com.liewmanchoi.pigeon.rpc.protocol.ProviderService;
import javafx.util.Pair;

import java.util.List;

/**
 * @author wangsheng
 * @date 2019/6/23
 */
public interface RegistryForGovernance {
    /**
     * queryProvidersAndInvokers
     *
     * @param serviceName 服务名称
     * @param appKey 服务唯一标识
     * @return javafx.util.Pair<java.util.List < com.liewmanchoi.pigeon.rpc.protocol.ProviderService>,java.util
     * .List<com.liewmanchoi.pigeon.rpc.protocol.InvokerService>>
     * @date 2019/6/23
     */
    Pair<List<ProviderService>, List<InvokerService>> queryProvidersAndInvokers(String serviceName, String appKey);
}
