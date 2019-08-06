package com.liewmanchoi.pigeon.rpc.protocol.api.protocol;

import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.ConsumerBean;
import com.liewmanchoi.pigeon.rpc.config.ProviderBean;
import com.liewmanchoi.pigeon.rpc.protocol.api.exporter.Exporter;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;

/**
 * 协议抽象接口
 *
 * @author wangsheng
 * @date 2019/6/30
 */
public interface Protocol {

  /**
   * 服务发布（服务暴露）
   *
   * @param invoker 待发布的服务提供者
   * @param providerBean 服务配置类对象
   * @return com.liewmanchoi.pigeon.rpc.protocol.api.exporter.Exporter<T>
   * @date 2019/6/30
   */
  <T> Exporter<T> export(Invoker<T> invoker, ProviderBean<T> providerBean) throws RPCException;

  /**
   * 引用服务
   *
   * @param consumerBean ConsumerBean
   * @param serviceURL ServiceURL
   * @return com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker<T>
   * @date 2019/6/30
   */
  <T> Invoker<T> refer(ConsumerBean<T> consumerBean, ServiceURL serviceURL) throws RPCException;

  /**
   * 根据接口名查找已经发布的服务
   *
   * @param interfaceName 接口名称
   * @return ProviderBean<?>
   * @date 2019/6/30
   */
  ProviderBean<?> referLocalService(String interfaceName) throws RPCException;

  /**
   * 关闭持有的所有连接
   *
   * @date 2019/6/30
   */
  void destroy();
}
