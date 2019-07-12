package com.liewmanchoi.pigeon.rpc.common.enumeration;

import com.liewmanchoi.pigeon.rpc.common.enumeration.support.ExtensionBaseType;
import com.liewmanchoi.pigeon.rpc.executor.api.PigeonExecutor;
import com.liewmanchoi.pigeon.rpc.executor.pigeon.PigeonExecutorService;

/**
 * @author wangsheng
 * @date 2019/7/12
 */
public enum ExecutorType implements ExtensionBaseType<PigeonExecutor> {

  /**
   * 线程池实现
   */
  THREAD_POOL(new PigeonExecutorService());
  private PigeonExecutor executor;

  ExecutorType(PigeonExecutor executor) {
    this.executor = executor;
  }

  @Override
  public PigeonExecutor getInstance() {
    return executor;
  }
}
