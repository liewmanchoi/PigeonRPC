package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.cluster.api.FaultToleranceHandler;
import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalancer;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.Protocol;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceRegistry;
import com.liewmanchoi.pigeon.rpc.serialization.api.Serializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局配置类。类中持有相关配置的实例
 *
 * @author wangsheng
 * @date 2019/6/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalConfig {
    private ApplicationConfig applicationConfig;
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    private ClusterConfig clusterConfig;

    // TODO: GlobalConfig待完成

    public Serializer getSerializer() {
        // TODO: getSerializer()
        return null;
    }

    public int getPort() {
        // TODO: getPort()
        return 0;
    }

    public ServiceRegistry getServiceRegistry() {
        return registryConfig.getRegistryInstance();
    }

    public Protocol getProtocol() {
        return protocolConfig.getProtocolInstance();
    }

    public LoadBalancer getLoadBalancer() {
        return clusterConfig.getLoadBalancerInstance();
    }

    public FaultToleranceHandler getFaultToleranceHandler() {
        return clusterConfig.getFaultToleranceHandlerInstance();
    }
}
