package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.executor.api.PigeonExecutor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangsheng
 * @date 2019/7/5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecutorConfig {
  public static final int DEFAULT_THREAD_NUMBERS = Runtime.getRuntime().availableProcessors();
  private int threadNum;
  private String type;
  private PigeonExecutor executorInstance;

  public int getThreads() {
    if (threadNum != 0) {
      return threadNum;
    }

    return DEFAULT_THREAD_NUMBERS;
  }

  public void close() {
    executorInstance.close();
  }
}
