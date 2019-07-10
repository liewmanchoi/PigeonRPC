package com.liewmanchoi.pigeon.rpc.protocol.pigeon;

import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import com.liewmanchoi.pigeon.rpc.config.ServiceConfig;
import com.liewmanchoi.pigeon.rpc.filter.Filter;
import com.liewmanchoi.pigeon.rpc.protocol.api.exporter.Exporter;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.support.AbstractRemoteProtocol;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceRegistry;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import com.liewmanchoi.pigeon.rpc.transport.api.Client;
import com.liewmanchoi.pigeon.rpc.transport.api.Server;
import com.liewmanchoi.pigeon.rpc.transport.pigeon.client.PigeonClient;
import com.liewmanchoi.pigeon.rpc.transport.pigeon.server.PigeonServer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
@Slf4j
public class PigeonProtocol extends AbstractRemoteProtocol {

  @Override
  protected Client doInitClient(ServiceURL serviceURL) {
    PigeonClient pigeonClient = new PigeonClient();
    pigeonClient.init(getGlobalConfig(), serviceURL);

    return pigeonClient;
  }

  @Override
  protected Server doOpenServer() {
    PigeonServer pigeonServer = new PigeonServer();
    pigeonServer.init(getGlobalConfig());
    pigeonServer.start();

    return pigeonServer;
  }

  @Override
  public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig)
      throws RPCException {
    PigeonExporter<T> pigeonExporter = new PigeonExporter<>();
    pigeonExporter.setInvoker(invoker);
    pigeonExporter.setServiceConfig(serviceConfig);

    putExporter(invoker.getInterface(), pigeonExporter);
    // 打开服务端
    openServer();
    // 暴露服务
    // 获取服务注册ServiceRegistry对象
    ServiceRegistry serviceRegistry = serviceConfig.getRegistryConfig().getRegistryInstance();
    try {
      String address =
          InetAddress.getLocalHost().getHostAddress() + ":" + getGlobalConfig().getPort();

      serviceRegistry.register(address, invoker.getInterfaceName());
    } catch (UnknownHostException e) {
      log.error("获取本地Host失败", e);
      throw new RPCException(e, ErrorEnum.READ_LOCALHOST_ERROR, "获取本地Host失败");
    }

    return pigeonExporter;
  }

  @Override
  public <T> Invoker<T> refer(ReferenceConfig<T> referenceConfig, ServiceURL serviceURL)
      throws RPCException {
    PigeonInvoker<T> invoker = new PigeonInvoker<>();
    invoker.setInterfaceClass(referenceConfig.getInterfaceClass());
    invoker.setGlobalConfig(getGlobalConfig());
    invoker.setClient(initClient(serviceURL));

    // 获取拦截器
    List<Filter> filters = referenceConfig.getFilters();
    if (filters == null || filters.isEmpty()) {
      return invoker;
    }
    // 构造包含拦截器调用链的InvokerDelegate匿名类对象
    return invoker.buildFilterChain(filters);
  }
}
