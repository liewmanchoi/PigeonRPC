package com.liewmanchoi.pigeon.rpc.cluster.api;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;

import java.util.List;

/**
 * 软负载均衡接口（属于consumer side)
 *
 * @author wangsheng
 * @date 2019/7/2
 */
public interface LoadBalancer {
    /**
     * 负载均衡选择函数
     * @param invokers Invoker对象列表
     * @param request RPC请求
     * @return Invoker
     */
    Invoker<?> select(List<Invoker> invokers, RPCRequest request);

    /**
     * 引用ClusterInvoker形式的服务
     * @param referenceConfig 引用配置类对象
     * @param <T> Interface类型
     * @return Invoker<T>
     */
    <T> ClusterInvoker<T> referCluster(ReferenceConfig<T> referenceConfig);
}
