package com.liewmanchoi.pigeon.rpc.protocol.api.invoker.support;

import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import lombok.Getter;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
public abstract class AbstractInvokerDelegate<T> extends AbstractInvoker<T> {
    @Getter
    private Invoker<T> delegate;

    public AbstractInvokerDelegate(Invoker<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int hashCode() {
        return delegate.getInterface().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Invoker)) {
            return false;
        }

        Invoker<?> other = (Invoker) obj;
        return delegate.getInterface().equals(other.getInterface());
    }

    @Override
    public boolean isAvailable() {
        return delegate.isAvailable();
    }

    @Override
    public Class<T> getInterface() {
        return delegate.getInterface();
    }

    @Override
    public String getInterfaceName() {
        return delegate.getInterfaceName();
    }

    @Override
    public ServiceURL getServiceURL() {
        return delegate.getServiceURL();
    }
}
