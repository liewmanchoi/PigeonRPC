package com.liewmanchoi;

import com.liewmanchoi.pigeon.rpc.config.ApplicationConfig;
import com.liewmanchoi.pigeon.rpc.config.ClusterConfig;
import com.liewmanchoi.pigeon.rpc.config.ProtocolConfig;
import com.liewmanchoi.pigeon.rpc.config.RegistryConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wangsheng
 * @date 2019/7/12
 */
@ConfigurationProperties(prefix = "pigeon")
@Data
public class PigeonProperties {

  private ProtocolConfig protocolConfig;
  private ApplicationConfig applicationConfig;
  private RegistryConfig registryConfig;
  private ClusterConfig clusterConfig;
}
