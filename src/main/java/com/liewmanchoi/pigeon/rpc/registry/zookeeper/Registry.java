package com.liewmanchoi.pigeon.rpc.registry.zookeeper;

import com.liewmanchoi.pigeon.rpc.protocol.InvokerInfo;
import com.liewmanchoi.pigeon.rpc.protocol.ProviderInfo;
import com.liewmanchoi.pigeon.rpc.registry.api.RegistryForInvoker;
import com.liewmanchoi.pigeon.rpc.registry.api.RegistryForProvider;
import com.liewmanchoi.pigeon.rpc.registry.zookeeper.utils.NodeUtils;
import com.liewmanchoi.pigeon.rpc.utils.PropertyConfigure;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author wangsheng
 * @date 2019/6/23
 */
@Slf4j
public class Registry implements RegistryForProvider, RegistryForInvoker {
  /** 存放在服务消费端的服务提供者缓存 */
  private static final Map<String, List<String>> PROVIDER_CACHE_MAP = new ConcurrentHashMap<>();

  private static final int SESSION_TIMOUT_MS = PropertyConfigure.getSessionTimeoutMs();
  private static final int CONNECTION_TIMOUT_MS = PropertyConfigure.getConnectionTimeoutMs();
  private static String ROOT_PATH = "pigeon";
  private static String PROVIDER_TYPE = "providers";
  private static String INVOKER_TYPE = "invokers";
  private static String CONNECT_STRING = PropertyConfigure.getConnectString();
  /** 使用Curator创建zookeeper客户端 */
  private static CuratorFramework zkClient =
      CuratorFrameworkFactory.builder()
          .connectString(CONNECT_STRING)
          .sessionTimeoutMs(SESSION_TIMOUT_MS)
          .connectionTimeoutMs(CONNECTION_TIMOUT_MS)
          .retryPolicy(new ExponentialBackoffRetry(1000, 3))
          .namespace(ROOT_PATH)
          .build();

  static {
    if (zkClient.checkExists() == null) {
      log.error("zookeeper client is not created!");
      throw new RuntimeException("zookeeper client is not created!");
    }
  }

  /** 禁止显式创建对象 */
  private Registry() {}

  /**
   * 单例模式
   *
   * @return Registry
   */
  public static Registry newInstance() {
    return InstanceHolder.singletonInstance;
  }

  @Override
  public void registerProvider(ProviderInfo providerInfo) throws Exception {
    if (providerInfo == null) {
      log.warn("cannot register null ProviderInfo object!");
      return;
    }

    // 将服务提供者信息注册到Zookeeper集群中
    String parentPath = NodeUtils.getProviderParentNode(providerInfo);
    String nodePath = NodeUtils.getProviderNode(providerInfo);

    try {
      zkClient
          .create()
          .creatingParentContainersIfNeeded()
          .withMode(CreateMode.PERSISTENT)
          .forPath(nodePath);
    } catch (Exception e) {
      log.error(String.format("create provider node: %s failed!", nodePath), e.getMessage());
      throw new Exception(e);
    }
  }

  @Override
  public void registerInvoker(InvokerInfo invokerInfo) throws Exception {
    if (invokerInfo == null) {
      log.warn("cannot register null InvokerInfo object!");
      return;
    }

    // 将服务消费者信息注册到Zookeeper集群中
    String nodePath = NodeUtils.getInvokerNode(invokerInfo);
    try {
      zkClient
          .create()
          .creatingParentContainersIfNeeded()
          .withMode(CreateMode.PERSISTENT)
          .forPath(nodePath);
    } catch (Exception e) {
      log.error(String.format("create invoker node: %s failed!", nodePath), e.getMessage());
      throw new Exception(e);
    }

    // 注册监听providers节点下子节点的变化
    monitorProviders(invokerInfo);
  }

  @Override
  public Map<String, List<String>> getProviderInfoCache() {
    return PROVIDER_CACHE_MAP;
  }

  private void monitorProviders(InvokerInfo invokerInfo) throws Exception {
    // 注册监听providers节点下子节点的变化
    String keyString = NodeUtils.getCommonPrefix(invokerInfo);
    // 如果/../providers节点存在，则监视它的子节点
    String monitoredNode = keyString + "/providers";
    if (zkClient.checkExists().forPath(monitoredNode) != null) {
      PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, monitoredNode, false);
      pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
      pathChildrenCache
          .getListenable()
          .addListener(
              (CuratorFramework client, PathChildrenCacheEvent event) -> {
                String node = event.getData().getPath();
                String addressAndPort = node.substring(node.lastIndexOf('/'));
                List<String> list = PROVIDER_CACHE_MAP.get(keyString);
                switch (event.getType()) {
                  case CHILD_ADDED:
                    if (list == null) {
                      list = new ArrayList<>();
                    }
                    list.add(addressAndPort);
                    PROVIDER_CACHE_MAP.put(keyString, list);
                    log.info(String.format("service provider: [%s] added", addressAndPort));
                    break;
                  case CHILD_REMOVED:
                    if (list.contains(addressAndPort)) {
                      list.remove(addressAndPort);
                      PROVIDER_CACHE_MAP.put(keyString, list);
                    }
                    log.info(String.format("service provider: [%s] removed", addressAndPort));
                    break;
                  default:
                    break;
                }
              });
    }
  }

  private static class InstanceHolder {
    static Registry singletonInstance = new Registry();
  }
}
