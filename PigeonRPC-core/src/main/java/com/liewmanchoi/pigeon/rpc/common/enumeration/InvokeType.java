package com.liewmanchoi.pigeon.rpc.common.enumeration;

/**
 * @author wangsheng
 * @date 2019/7/1
 */
public enum InvokeType {
  /**
   * Oneway调用
   */
  ONEWAY,
  /** 同步调用 */
  SYNC,
  /** 异步调用 */
  ASYNC,
  /** 回调 */
  CALLBACK
}
