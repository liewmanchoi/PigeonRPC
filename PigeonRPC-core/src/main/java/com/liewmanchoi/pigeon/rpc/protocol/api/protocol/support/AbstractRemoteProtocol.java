package com.liewmanchoi.pigeon.rpc.protocol.api.protocol.support;

import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import com.liewmanchoi.pigeon.rpc.transport.api.Client;
import com.liewmanchoi.pigeon.rpc.transport.api.Server;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
@Slf4j
public abstract class AbstractRemoteProtocol extends AbstractProtocol {

  /**
   * address-client
   */
  private Map<String, Client> clientMap = new ConcurrentHashMap<>();

  private Map<String, Object> lockMap = new ConcurrentHashMap<>();
  private Server server;

  public final Client initClient(ServiceURL serviceURL) {
    String address = serviceURL.getAddress();
    lockMap.putIfAbsent(address, new Object());
    synchronized (lockMap.get(address)) {
      if (clientMap.containsKey(address)) {
        // client已经初始化
        return clientMap.get(address);
      }

      Client client = doInitClient(serviceURL);
      clientMap.put(address, client);
      // TODO: 是否需要移除lockMap中对应的键值对
      return client;
    }
  }

  /**
   * 初始化客户端（不需要考虑线程安全）
   *
   * @param serviceURL ServiceURL
   * @return Client
   */
  protected abstract Client doInitClient(ServiceURL serviceURL);

  protected final synchronized void openServer() {
    if (server != null) {
      log.warn("服务器已经打开，不能重复开启");
      return;
    }

    server = doOpenServer();
  }

  /**
   * 打开服务端
   *
   * @return Server
   */
  protected abstract Server doOpenServer();

  /**
   * 更新客户端配置
   *
   * @param serviceURL ServiceURL
   */
  public final void updateServiceURL(ServiceURL serviceURL) {
    String address = serviceURL.getAddress();
    if (clientMap.containsKey(address)) {
      clientMap.get(address).updateServiceURL(serviceURL);

      // 更新lockMap
      lockMap.remove(address);
    }

    log.error("无法找到地址为[{}]的客户端", serviceURL);
  }

  public void closeClient(String address) {
    Client client = clientMap.remove(address);
    lockMap.remove(address);

    if (client != null) {
      log.info("关闭客户端[{}]", client.getServiceURL());
      client.close();

      return;
    }

    log.warn("客户端[{}]不存在", address);
  }

  @Override
  public void destroy() {
    clientMap.values().forEach(Client::close);
    lockMap.clear();
    if (server != null) {
      server.close();
    }
  }
}
