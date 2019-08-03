package com.liewmanchoi.pigeon.rpc.registry.zookeeper;

import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.registry.api.EventHandler;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceRegistry;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author wangsheng
 * @date 2019/7/3
 */
@Slf4j
public class ZookeeperServiceRegistry implements ServiceRegistry {
  private static final String ROOT_PATH = "pigeon";
  private CuratorFramework zkClient;
  /** 集群连接地址字符串 */
  private String address;

  private CountDownLatch latch = new CountDownLatch(1);

  public ZookeeperServiceRegistry(String address) {
    this.address = address;
  }

  private static String generatePath(String interfaceName) {
    return ROOT_PATH + "/" + interfaceName;
  }

  @Override
  public void init() {
    // 统一限定命名空间
    zkClient =
        CuratorFrameworkFactory.builder()
            .connectString(address)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .namespace(ROOT_PATH)
            .build();
    // 启动CuratorFramework，该接口为同步接口，能够阻塞直至成功建立连接或者失败
    zkClient.start();

    // 添加回调，处理连接事件
    zkClient
        .getConnectionStateListenable()
        .addListener(
            (CuratorFramework client, ConnectionState newState) -> {
              switch (newState) {
                case CONNECTED:
                  log.info(">>>   连接zookeeper集群成功   <<<");
                  latch.countDown();
                  break;
                case RECONNECTED:
                  log.info(">>>   重新连接zookeeper集群   <<<");
                  break;
                default:
                  log.error(">>>   与zookeeper集群的连接发生异常   <<<");
                  break;
              }
            });
    // 阻塞程序，直至成功建立连接
    try {
      latch.await();
    } catch (InterruptedException e) {
      log.error(">>>   发生中断异常   <<<");
      throw new RuntimeException("发生中断异常");
    }
  }

  @Override
  public void register(String address, String interfaceName) {
    String path = generatePath(interfaceName);
    String node = path + "/" + address;
    // 创建节点和数据
    try {
      zkClient
          .create()
          .creatingParentContainersIfNeeded()
          // 创建的节点必须是临时节点
          .withMode(CreateMode.EPHEMERAL)
          .forPath(node, address.getBytes());
    } catch (Exception e) {
      log.error("服务[{}-{}]注册发生异常{}", interfaceName, address, e);
      throw new RPCException(ErrorEnum.SERVICE_REGISTER_FAILURE, "服务注册失败", e);
    }
  }

  /**
   * 服务发现（用于consumer端）
   *
   * @param interfaceName 接口名称
   */
  @Override
  public void discover(String interfaceName, EventHandler eventHandler) {
    final String path = generatePath(interfaceName);
    // 监听path节点(/pigeon/interfaceName)下子节点的变化
    // 1. path节点不存在，则等待创建后再监听
    final NodeCache nodeCache = new NodeCache(zkClient, path);
    try {
      nodeCache.start(true);
      // 如果节点创建，则启动对子节点的监听
      nodeCache
          .getListenable()
          .addListener(
              () -> {
                log.info("[{}]节点已经被成功创建，开始监听子节点事件", path);

                // 2. 监听子节点内容的变化
                doDiscover(path, eventHandler);
              });
    } catch (Exception e) {
      log.error("无法启动对节点的监听", e);
      throw new RPCException(ErrorEnum.SERVICE_DISCOVER_FAILURE, "服务发现故障", e);
    }
  }

  private void doDiscover(String path, EventHandler eventHandler) throws Exception {
    PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);
    pathChildrenCache.start(StartMode.POST_INITIALIZED_EVENT);

    pathChildrenCache
        .getListenable()
        .addListener(
            (CuratorFramework client, PathChildrenCacheEvent event) -> {
              byte[] bytes = event.getData().getData();
              ServiceURL serviceURL = ServiceURL.parse(new String(bytes, StandardCharsets.UTF_8));

              switch (event.getType()) {
                case CHILD_ADDED:
                  eventHandler.add(serviceURL);
                  break;
                case CHILD_UPDATED:
                  eventHandler.update(serviceURL);
                  break;
                case CHILD_REMOVED:
                  eventHandler.remove(serviceURL);
                  break;
                default:
                  break;
              }
            });
  }

  @Override
  public void close() {
    log.info("关闭与Zookeeper集群的连接");
    zkClient.close();
  }
}
