package com.liewmanchoi.pigeon.rpc.protocol.api.invoker;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;

/**
 * 抽象的服务调用者
 *
 * <p>包括consumer端的代理实例InvokerDelegate和provider端的服务实现类实例Exporter <br>
 * 对于consumer端而言，一个协议Invoker（由Protocol调用其refer生成的实例）对应于一个接口的一个服务器实现(interface, address) <br>
 * 一个ClusterInvoker对应于一个接口的所有服务器实现(interface)
 *
 * @author wangsheng
 * @date 2019/6/26
 */
public interface Invoker<T> {

  /**
   * getInterface
   *
   * @return java.lang.Class<T>
   * @date 2019/6/26
   */
  Class<T> getInterface();

  /**
   * getInterfaceName
   *
   * @return java.lang.String
   * @date 2019/6/26
   */
  String getInterfaceName();

  /**
   * @param request 调用参数
   * @return RPCResponse
   * @throws RPCException 自定义异常
   */
  RPCResponse invoke(RPCRequest request) throws RPCException;

  /**
   * 返回注册地址
   *
   * <p>本地服务返回本地IP地址 <br>
   * 远程服务返回注册中心的ServiceURL
   *
   * @return com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL
   * @date 2019/6/26
   */
  ServiceURL getServiceURL();

  /**
   * Invoker是否可用
   *
   * @return boolean
   * @date 2019/6/30
   */
  boolean isAvailable();
}
