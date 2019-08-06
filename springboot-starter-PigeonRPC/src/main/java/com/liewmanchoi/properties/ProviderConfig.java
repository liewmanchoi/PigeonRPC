package com.liewmanchoi.properties;

import lombok.Data;

/**
 * @author wangsheng
 * @date 2019/8/3
 */
@Data
public class ProviderConfig {
  private Integer port;
  private Integer workerThreads;
}
