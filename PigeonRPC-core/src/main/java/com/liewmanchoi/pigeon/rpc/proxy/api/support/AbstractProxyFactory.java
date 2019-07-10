package com.liewmanchoi.pigeon.rpc.proxy.api.support;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequestWrapper;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.common.utils.GlobalRecycler;
import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.support.AbstractInvoker;
import com.liewmanchoi.pigeon.rpc.proxy.api.ProxyFactory;
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
  private Map<Class<?>, Object> cacheMap = new ConcurrentHashMap<>();

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
    RPCRequest rpcRequest = GlobalRecycler.reuse(RPCRequest.class);
    log.info(
        "调用服务[interfaceName: {}, methodName: {}, argTypes: {}, args: {}]",
        interfaceName,
        methodName,
        argTypes,
        args);

    rpcRequest.setRequestId(UUID.randomUUID().toString());
    rpcRequest.setInterfaceName(interfaceName);
    rpcRequest.setMethodName(methodName);
    rpcRequest.setArgTypes(argTypes);
    rpcRequest.setArgs(args);

    RPCRequestWrapper rpcRequestWrapper =
        RPCRequestWrapper.builder()
            .rpcRequest(rpcRequest)
            .referenceConfig(ReferenceConfig.getConfigByInterfaceName(interfaceName))
            .build();
    RPCResponse rpcResponse = invoker.invoke(rpcRequestWrapper);

    Object result = null;
    if (rpcResponse != null) {
      result = rpcResponse.getResult();
      // 回收response
      rpcResponse.recycle();
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
    Class<T> anInterface = invoker.getInterface();
    if (cacheMap.containsKey(anInterface)) {
      return (T) cacheMap.get(anInterface);
    }

    // 如果缓存中不存在，则创建之
    T t = doCreateProxy(anInterface, invoker);
    cacheMap.put(anInterface, t);
    return t;
  }

  @Override
  public <T> Invoker<T> getInvoker(T proxy, Class<T> clazz) {
    return new AbstractInvoker<T>() {
      @Override
      public RPCResponse invoke(RPCRequestWrapper rpcRequestWrapper) throws RPCException {
        RPCResponse response = GlobalRecycler.reuse(RPCResponse.class);

        RPCRequest rpcRequest = rpcRequestWrapper.getRpcRequest();
        try {
          Method method =
              proxy.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getArgTypes());
          response.setRequestId(rpcRequest.getRequestId());

          // 核心：使用反射生成的代理类进行调用
          Object result = method.invoke(proxy, rpcRequest.getArgs());
          response.setResult(result);
        } catch (Exception e) {
          // 将异常放入到response内
          response.setCause(e);
          log.error(
              "调用[requestId: {}, interfaceName: {}, methodName: {}, argTypes: {}, args: {}]失败，抛出异常[{}]",
              rpcRequest.getRequestId(),
              rpcRequest.getInterfaceName(),
              rpcRequest.getMethodName(),
              rpcRequest.getArgTypes(),
              rpcRequest.getArgs(),
              e);
        }

        return response;
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
