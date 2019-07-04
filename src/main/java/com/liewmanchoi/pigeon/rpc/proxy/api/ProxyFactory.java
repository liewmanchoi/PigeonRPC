package com.liewmanchoi.pigeon.rpc.proxy.api;

import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;

/**
 * @author wangsheng
 * @date 2019/7/2
 */
public interface ProxyFactory {
  /**
   * 创建代理类
   *
   * @param invoker Invoker
   * @param <T> 接口类型
   * @return 接口代理类
   */
  <T> T createProxy(Invoker<T> invoker);

  /**
   * 获取对应的Invoker类对象
   *
   * @param proxy 代理类对象
   * @param clazz Class类对象
   * @param <T> 接口类型
   * @return Invoker<T>
   */
  <T> Invoker<T> getInvoker(T proxy, Class<T> clazz);
}
