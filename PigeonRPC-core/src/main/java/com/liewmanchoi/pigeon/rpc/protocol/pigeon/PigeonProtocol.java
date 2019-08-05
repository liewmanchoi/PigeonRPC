package com.liewmanchoi.pigeon.rpc.protocol.pigeon;

import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import com.liewmanchoi.pigeon.rpc.config.ServiceConfig;
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
    pigeonClient.init(commonBean, serviceURL);

    return pigeonClient;
  }

  @Override
  protected Server doOpenServer() {
    PigeonServer pigeonServer = new PigeonServer();
    pigeonServer.init(commonBean, port);
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
      String address = InetAddress.getLocalHost().getHostAddress() + ":" + port;

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
    invoker.setClient(initClient(serviceURL));

    return invoker;
  }
}
