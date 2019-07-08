package com.liewmanchoi.pigeon.rpc.executor.api;

/**
 * @author wangsheng
 * @date 2019/7/5
 */
public interface PigeonExecutor {

  /**
   * 初始化线程池
   *
   * @param threadNum 线程数目
   */
  void init(int threadNum);

  /**
   * 向线程池中提交任务
   *
   * @param runnable 任务
   */
  void submit(Runnable runnable);

  /** 关闭线程池 */
  void close();
}
