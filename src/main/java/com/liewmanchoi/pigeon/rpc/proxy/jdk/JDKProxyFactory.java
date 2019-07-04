package com.liewmanchoi.pigeon.rpc.proxy.jdk;

import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import com.liewmanchoi.pigeon.rpc.proxy.api.support.AbstractProxyFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author wangsheng
 * @date 2019/7/4
 */
public class JDKProxyFactory extends AbstractProxyFactory {
  @SuppressWarnings("unchecked")
  @Override
  public <T> T doCreateProxy(Class<T> interfaceClass, Invoker<T> invoker) {
    return (T)
        Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class[] {interfaceClass},
            (Object proxy, Method method, Object[] args) ->
                JDKProxyFactory.this.invokeProxyMethod(invoker, method, args));
  }
}
