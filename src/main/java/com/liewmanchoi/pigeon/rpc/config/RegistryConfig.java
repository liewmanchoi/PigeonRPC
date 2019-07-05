package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.registry.api.ServiceRegistry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistryConfig {
  private String type;
  private String address;
  private ServiceRegistry registryInstance;

  public void init() {
    if (registryInstance != null) {
      registryInstance.init();
    }
  }

  public void close() {
    if (registryInstance != null) {
      registryInstance.close();
    }
  }
}
