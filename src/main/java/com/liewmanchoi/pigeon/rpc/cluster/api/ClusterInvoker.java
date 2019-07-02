package com.liewmanchoi.pigeon.rpc.cluster.api;

import com.liewmanchoi.pigeon.rpc.common.context.RPCThreadPrivateContext;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequestWrapper;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.GlobalConfig;
import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.Protocol;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.support.AbstractRemoteProtocol;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对应于一个interface的所有服务器实现
 *
 * @author wangsheng
 * @date 2019/7/1
 */
@Slf4j
public class ClusterInvoker<T> implements Invoker<T> {
    private Class<T> interfaceClass;
    private GlobalConfig globalConfig;

    /**
     * ClusterInvoker中包含的invoker列表
     * 键为invoker所在的地址
     */
    private Map<String, Invoker<T>> invokerMap = new ConcurrentHashMap<>();

    public ClusterInvoker(Class<T> interfaceClass, GlobalConfig globalConfig) {
        this.interfaceClass = interfaceClass;
        this.globalConfig = globalConfig;
        init();
    }

    private void init() {
        // TODO: ClusterInvoker#init()方法
    }

    @Override
    public RPCResponse invoke(RPCRequestWrapper rpcRequestWrapper) throws RPCException {
        try {
            return invokeOnce(getInvokers(), rpcRequestWrapper);
        } catch (RPCException e) {
            // 如果发生错误，则开启容错模式，显然容错只会对同步调用有效，因为异步调用的返回值为null，无法判断是否发生了异常
            return globalConfig.getFaultToleranceHandler().handle(this, rpcRequestWrapper, e);
        }
    }

    /**
     * 增加新的或更新现有的ServiceURL
     * @param serviceURL ServiceURL
     */
    @SuppressWarnings("unchecked")
    private synchronized void addOrUpdate(ServiceURL serviceURL) {
        Protocol protocol = globalConfig.getProtocol();
        String address = serviceURL.getAddress();
        String interfaceName = getInterfaceName();

        if (invokerMap.containsKey(address)) {
            // 如果address服务已经被引用
            if (protocol instanceof AbstractRemoteProtocol) {
                // 只有更新远程服务才有意义
                // 一个Client对应于一个ServiceURL，具体操作由Protocol负责交互
                log.info("更新服务器配置为[{}]，对应的接口为[{}]", serviceURL, interfaceName);
                ((AbstractRemoteProtocol) protocol).updateServiceConfig(serviceURL);
            }
            return;
        }
        // 如果ServiceURL对应的服务尚未被引用
        log.info("添加Invoker，服务器配置为[{}]，对应的接口为[{}]", serviceURL, interfaceName);
        // 引用服务
        Invoker invoker = protocol.refer(ReferenceConfig.getConfigByInterfaceName(interfaceName), serviceURL);
        // 更新invokerMap
        invokerMap.put(address, (Invoker<T>) invoker);
    }

    public synchronized void removeNotExisted(List<ServiceURL> serviceURLS) {
        // TODO: removeNotExisted()方法
    }

    private Invoker<?> doSelect(List<Invoker> invokers, RPCRequestWrapper rpcRequestWrapper) {
        LoadBalancer loadBalancer = globalConfig.getLoadBalancer();
        RPCRequest request = rpcRequestWrapper.getRpcRequest();
        while (!invokers.isEmpty()) {
            Invoker invoker = loadBalancer.select(invokers, request);

            if (!invoker.isAvailable()) {
                log.warn("挑选的Invoker不可用：配置[{}]，接口[{}]", invoker.getServiceURL(), invoker.getInterfaceName());
                invokers.remove(invoker);
                continue;
            }
            return invoker;
        }
        // 如果无法查找到可用服务提供者，则抛出异常
        log.error("无法查到到可用的Invoker");
        throw new RPCException(ErrorEnum.NO_SERVER_AVAILABLE, "无法查找到可用的服务提供者");
    }

    /**
     * 单次调用，如果发生错误，直接抛出（供后续容错处理时调用）
     * @param rpcRequestWrapper request
     * @return RPCResponse
     */
    private RPCResponse invokeOnce(List<Invoker> invokers, RPCRequestWrapper rpcRequestWrapper) {
        // 选择可用的Invoker
        Invoker<?> invoker = doSelect(invokers, rpcRequestWrapper);
        RPCThreadPrivateContext.getContext().setInvoker(invoker);
        RPCResponse response = invoker.invoke(rpcRequestWrapper);

        if (response == null) {
            // response为空，说明是异步调用ASYNC
            return null;
        }

        if (response.hasError()) {
            Throwable cause = response.getCause();

            // 回收response对象
            response.recycle();
            log.error("服务调用发生异常：[{}]", rpcRequestWrapper.getRpcRequest());
            throw new RPCException(cause, ErrorEnum.SERVICE_INVOCATION_FAILURE, "服务调用失败");
        }

        return response;
    }

    @Override
    public Class<T> getInterface() {
        return interfaceClass;
    }

    @Override
    public String getInterfaceName() {
        return interfaceClass.getName();
    }

    public List<Invoker> getInvokers() {
        return new ArrayList<>(invokerMap.values());
    }

    @Override
    public ServiceURL getServiceURL() {
        throw new UnsupportedOperationException("ClusterInvoker不支持getServiceURL()方法");
    }

    @Override
    public boolean isAvailable() {
        for (Invoker<T> invoker : invokerMap.values()) {
            if (!invoker.isAvailable()) {
                return false;
            }
        }

        return true;
    }
}
