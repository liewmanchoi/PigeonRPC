package com.liewmanchoi.pigeon.rpc.registry.api;

/**
 * @author wangsheng
 * @date 2019/7/4
 */
public interface EventHandler {
  void add(ServiceURL serviceURL);
  void update(ServiceURL serviceURL);
  void remove(ServiceURL serviceURL);
}
