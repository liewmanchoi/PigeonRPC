package com.liewmanchoi.pigeon.rpc.registry.api;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
public interface ServiceRegistry {
  /** 初始化 */
  void init();

  /**
   * 服务发现
   *
   * @param interfaceName 接口名称
   */
  void discover(String interfaceName, EventHandler eventHandler);

  /**
   * 服务注册
   *
   * @param address 网络地址
   * @param interfaceName 接口名称
   */
  void register(String address, String interfaceName);

  /** 关闭 */
  void close();
}
