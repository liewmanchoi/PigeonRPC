package com.liewmanchoi.pigeon.rpc.proxy.api.support;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.common.utils.GlobalRecycler;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import com.liewmanchoi.pigeon.rpc.proxy.api.ProxyFactory;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/7/4
 */
@Slf4j
public abstract class AbstractProxyFactory implements ProxyFactory {
  private final Map<Class<?>, Object> cacheMap = new ConcurrentHashMap<>();

  public abstract <T> T doCreateProxy(Class<T> interfaceClass, Invoker<T> invoker);

  private Object invokeProxyMethod(
      Invoker<?> invoker,
      String interfaceName,
      String methodName,
      String[] argTypes,
      Object[] args) {
    assert (argTypes.length == args.length);

    final String TO_STRING = "toString";
    final String HASHCODE = "hashCode";
    final String EQUALS = "equals";

    if (TO_STRING.equals(methodName) && args.length == 0) {
      return invoker.toString();
    }

    if (HASHCODE.equals(methodName) && args.length == 0) {
      return invoker.hashCode();
    }

    if (EQUALS.equals(methodName) && args.length == 1) {
      return invoker.equals(args[0]);
    }

    // 构建RPCRequest
    RPCRequest request = GlobalRecycler.reuse(RPCRequest.class);
    request.setRequestId(UUID.randomUUID().toString());
    request.setInterfaceName(interfaceName);
    request.setMethodName(methodName);
    request.setArgTypes(argTypes);
    request.setArgs(args);

    log.info(
        ">>>   调用服务[interfaceName: {}, methodName: {}, argTypes: {}, args: {}]   <<<",
        interfaceName,
        methodName,
        argTypes,
        args);

    RPCResponse response = invoker.invoke(request);

    Object result = null;
    if (response != null) {
      result = response.getResult();
      // 回收response
      response.recycle();
    }

    return result;
  }

  protected Object invokeProxyMethod(Invoker<?> invoker, Method method, Object[] args) {
    Class<?>[] argTypes = method.getParameterTypes();
    String[] argTypeStrings = new String[argTypes.length];
    for (int i = 0; i < argTypes.length; i++) {
      argTypeStrings[i] = argTypes[i].getName();
    }

    String interfaceName = method.getDeclaringClass().getName();
    return invokeProxyMethod(invoker, interfaceName, method.getName(), argTypeStrings, args);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T createProxy(Invoker<T> invoker) {
    Class<T> interfaceClass = invoker.getInterface();
    if (cacheMap.containsKey(interfaceClass)) {
      return (T) cacheMap.get(interfaceClass);
    }

    // 如果没有interfaceClass对应的invoker，则创建之
    T t = doCreateProxy(interfaceClass, invoker);
    cacheMap.put(interfaceClass, t);
    return t;
  }

  /** 供Provider使用 */
  @Override
  public <T> Invoker<T> getInvoker(T bean, Class<T> clazz) {
    return new Invoker<T>() {
      @Override
      public RPCResponse invoke(RPCRequest request) throws RPCException {
        RPCResponse response = GlobalRecycler.reuse(RPCResponse.class);

        try {
          Method method = bean.getClass().getMethod(request.getMethodName(), request.getArgTypes());
          response.setRequestId(request.getRequestId());

          // 核心：使用反射进行方法调用
          Object result = method.invoke(bean, request.getArgs());
          response.setResult(result);

          log.info(
              ">>>   调用[requestId: {}, interfaceName: {}, methodName: {}, argTypes: {}, args: {}]成功   <<<",
              request.getRequestId(),
              request.getInterfaceName(),
              request.getMethodName(),
              request.getArgTypes(),
              request.getArgs());
        } catch (Exception e) {
          // 将异常放入到response内
          response.setCause(e);
          log.error(
              ">>>   调用[requestId: {}, interfaceName: {}, methodName: {}, argTypes: {}, args: {}]失败，抛出异常[{}]   <<<",
              request.getRequestId(),
              request.getInterfaceName(),
              request.getMethodName(),
              request.getArgTypes(),
              request.getArgs(),
              e);
        }

        return response;
      }

      @Override
      public ServiceURL getServiceURL() {
        return ServiceURL.DEFAULT_SERVICE_URL;
      }

      @Override
      public Class<T> getInterface() {
        return clazz;
      }

      @Override
      public String getInterfaceName() {
        return clazz.getName();
      }

      @Override
      public boolean isAvailable() {
        return false;
      }
    };
  }
}
