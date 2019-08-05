package com.liewmanchoi.pigeon.rpc.cluster;

import com.liewmanchoi.pigeon.rpc.cluster.api.FaultToleranceHandler;
import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalancer;
import com.liewmanchoi.pigeon.rpc.common.context.RpcContext;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.ConsumerBean;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.support.AbstractInvoker;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.Protocol;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.support.AbstractRemoteProtocol;
import com.liewmanchoi.pigeon.rpc.registry.api.EventHandler;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceRegistry;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * 对应于一个interface的所有服务器实现的抽象 <br>
 * ClusterInvoker属于consumer side
 *
 * @author wangsheng
 * @date 2019/7/1
 */
@Slf4j
public class ClusterInvoker<T> extends AbstractInvoker<T> {
  /** 键为invoker所在的地址，值为对应的Invoker */
  private Map<String, Invoker<T>> invokerMap = new ConcurrentHashMap<>();

  private void init() {
    if (commonBean.getProtocol() instanceof AbstractRemoteProtocol) {
      ServiceRegistry serviceRegistry = commonBean.getServiceRegistry();
      // 进行服务发现
      serviceRegistry.discover(getInterfaceName(), new EventHandlerAdapter());
    }
  }

  @Override
  public RPCResponse invoke(RPCRequest request) throws RPCException {
    try {
      return invokeOnce(getInvokers(), request);
    } catch (RPCException e) {
      // 如果发生错误，则开启容错模式，显然容错只会对同步调用有效，因为异步调用的返回值为null，无法判断是否发生了异常
      // 容错处理器
      FaultToleranceHandler handler = consumerBean.getFaultToleranceHandler();
      return handler.handle(this, request, e);
    }
  }

  private Invoker<?> doSelect(List<Invoker> invokers, RPCRequest request) {
    LoadBalancer loadBalancer = consumerBean.getLoadBalancer();
    while (!invokers.isEmpty()) {
      Invoker invoker = loadBalancer.select(invokers, request);

      if (!invoker.isAvailable()) {
        log.warn(
            "挑选的Invoker不可用：配置[{}]，接口[{}]", invoker.getServiceURL(), invoker.getInterfaceName());
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
   *
   * @param request 请求
   * @return RPCResponse
   */
  private RPCResponse invokeOnce(List<Invoker> invokers, RPCRequest request) {
    // 选择可用的Invoker
    Invoker<?> invoker = doSelect(invokers, request);
    RpcContext.getContext().setInvoker(invoker);
    RPCResponse response = invoker.invoke(request);

    // response为空，说明是异步调用ASYNC
    if (response != null && response.hasError()) {
      Throwable cause = response.getCause();

      // 回收response对象
      response.recycle();
      log.error("服务调用发生异常：[{}]", request);
      throw new RPCException(cause, ErrorEnum.SERVICE_INVOCATION_FAILURE, "服务调用失败");
    }

    log.info("requestId为[{}]的调用成功", request.getRequestId());
    return response;
  }

  @Override
  public Class<T> getInterface() {
    return consumerBean.getInterfaceClass();
  }

  @Override
  public String getInterfaceName() {
    return consumerBean.getInterfaceName();
  }

  private List<Invoker> getInvokers() {
    return new ArrayList<>(invokerMap.values());
  }

  @Override
  public ServiceURL getServiceURL() {
    throw new UnsupportedOperationException(">>>   ClusterInvoker不支持getServiceURL()方法   <<<");
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

  private class EventHandlerAdapter implements EventHandler {
    @SuppressWarnings("unchecked")
    @Override
    public void add(ServiceURL serviceURL) {
      Protocol protocol = commonBean.getProtocol();
      String address = serviceURL.getAddress();
      String interfaceName = getInterfaceName();

      if (!invokerMap.containsKey(address)) {
        log.info("添加Invoker，服务器配置为[{}]，对应的接口为[{}]", serviceURL, interfaceName);
        // 引用服务
        Invoker invoker = protocol.refer(ConsumerBean.getBeanByName(interfaceName), serviceURL);
        // 更新invokerMap
        invokerMap.put(address, (Invoker<T>) invoker);
      }
    }

    @Override
    public void update(ServiceURL serviceURL) {
      Protocol protocol = commonBean.getProtocol();
      String address = serviceURL.getAddress();
      String interfaceName = getInterfaceName();

      if (invokerMap.containsKey(address)) {
        // 如果address服务已经被引用
        if (protocol instanceof AbstractRemoteProtocol) {
          // 只有更新远程服务才有意义
          // 一个Client对应于一个ServiceURL，具体操作由Protocol负责交互
          log.info("更新服务器配置为[{}]，对应的接口为[{}]", serviceURL, interfaceName);
          ((AbstractRemoteProtocol) protocol).updateServiceURL(serviceURL);
        }
      }
    }

    @Override
    public void remove(ServiceURL serviceURL) {
      // serviceURL对应的服务器出故障了，应该关闭
      Protocol protocol = commonBean.getProtocol();
      String address = serviceURL.getAddress();
      String interfaceName = getInterfaceName();

      invokerMap.remove(address);
      log.warn("删除Invoker，地址为[{}]，接口名称为[{}]", address, interfaceName);
      // 关闭address对应的服务器
      if (protocol instanceof AbstractRemoteProtocol) {
        ((AbstractRemoteProtocol) protocol).closeClient(address);
      }
    }
  }
}
