package com.liewmanchoi.pigeon.rpc.executor.pigeon;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.liewmanchoi.pigeon.rpc.executor.api.PigeonExecutor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wangsheng
 * @date 2019/7/5
 */
public class PigeonExecutorService implements PigeonExecutor {
  private ExecutorService executorService;

  @Override
  public void init(int threadNum) {
    ThreadFactory namedThreadFactory =
        new ThreadFactoryBuilder().setNameFormat("PigeonRPC-ThreadPool-%d").build();
    executorService =
        new ThreadPoolExecutor(
            threadNum,
            threadNum,
            0,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            namedThreadFactory,
            new ThreadPoolExecutor.CallerRunsPolicy());
  }

  @Override
  public void submit(Runnable runnable) {
    executorService.submit(runnable);
  }

  @Override
  public void close() {
    executorService.shutdown();
  }
}
