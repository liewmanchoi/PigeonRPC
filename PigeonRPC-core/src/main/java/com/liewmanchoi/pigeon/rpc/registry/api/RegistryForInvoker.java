package com.liewmanchoi.pigeon.rpc.registry.api;

import com.liewmanchoi.pigeon.rpc.protocol.InvokerInfo;
import java.util.List;
import java.util.Map;

/**
 * @author wangsheng
 * @date 2019/6/22
 */
public interface RegistryForInvoker {

  /**
   * 向注册中心注册服务消费者信息
   *
   * @param invokerService 服务消费者信息
   * @throws Exception 异常
   * @date 2019/6/22
   */
  void registerInvoker(final InvokerInfo invokerService) throws Exception;

  /**
   * 获取服务提供者信息
   *
   * @return java.util.Map<java.lang.String, java.util.List < com.liewmanchoi.pigeon.rpc.protocol.ProviderService>>
   * @date 2019/6/22
   */
  Map<String, List<String>> getProviderInfoCache();
}
