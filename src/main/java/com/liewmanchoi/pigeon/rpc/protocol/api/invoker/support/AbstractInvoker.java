package com.liewmanchoi.pigeon.rpc.protocol.api.invoker.support;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequestWrapper;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.GlobalConfig;
import com.liewmanchoi.pigeon.rpc.filter.Filter;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
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
    private Class<T> interfaceClass;

    @Getter
    @Setter
    private GlobalConfig globalConfig;

    @Override
    public Class<T> getInterface() {
        return interfaceClass;
    }

    @Override
    public String getInterfaceName() {
        return interfaceClass.getName();
    }

    @Override
    public ServiceURL getServiceURL() {
        return ServiceURL.DEFAULT_SERVICE_URL;
    }

    /**
     * 构建Filter链
     * @param filters Filter对象列表
     * @return Invoker
     */
    public Invoker<T> buildFilterChain(List<Filter> filters) {
        Invoker<T> last = this;

        if (!filters.isEmpty()) {
            for (int i = filters.size() - 1; i >= 0; --i) {
                final Filter filter = filters.get(i);
                final Invoker<T> next = last;

                last = new AbstractInvokerDelegate<T>(this) {
                    @Override
                    public RPCResponse invoke(RPCRequestWrapper rpcRequestWrapper) throws RPCException {
                        return filter.invoke(next, rpcRequestWrapper);
                    }
                };
            }
        }

        return last;
    }
}
