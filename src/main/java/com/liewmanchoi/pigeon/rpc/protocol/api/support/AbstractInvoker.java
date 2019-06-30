package com.liewmanchoi.pigeon.rpc.protocol.api.support;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.GlobalConfig;
import com.liewmanchoi.pigeon.rpc.filter.Filter;
import com.liewmanchoi.pigeon.rpc.protocol.api.InvokeArgs;
import com.liewmanchoi.pigeon.rpc.protocol.api.Invoker;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
@Slf4j
public abstract class AbstractInvoker<T> implements Invoker<T> {
    @Setter
    private Class<T> interfaceClazz;

    @Getter
    @Setter
    private GlobalConfig globalConfig;

    @Override
    public Class<T> getInterface() {
        return interfaceClazz;
    }

    @Override
    public String getInterfaceName() {
        return interfaceClazz.getName();
    }

    @Override
    public ServiceURL getServiceURL() {
        return ServiceURL.DEFAULT_SERVICE_URL;
    }

    // TODO: buildFilterChain()
    /**
     * 构建Filter链
     * @param filters Filter对象列表
     * @return Invoker
     */
    public Invoker<T> buildFilterChain(List<Filter> filters) {
        return null;
    }
}
