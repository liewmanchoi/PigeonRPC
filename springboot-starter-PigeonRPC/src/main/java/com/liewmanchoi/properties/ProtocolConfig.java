package com.liewmanchoi.properties;

import lombok.Data;

/**
 * @author wangsheng
 * @date 2019/8/3
 */
@Data
public class ProtocolConfig {
  private String proxy;
  private String serializer;
  private String protocol;
  private String executor;
  private Integer port;
  private Integer threads;
}
