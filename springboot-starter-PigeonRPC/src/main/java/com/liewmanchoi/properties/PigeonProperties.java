package com.liewmanchoi.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author wangsheng
 * @date 2019/8/6
 */
@Data
@ConfigurationProperties(value = "pigeon")
public class PigeonProperties {
  @NestedConfigurationProperty private RegistryConfig registry = new RegistryConfig();
  @NestedConfigurationProperty private ConsumerConfig consumer = new ConsumerConfig();
  @NestedConfigurationProperty private ProtocolConfig protocol = new ProtocolConfig();
  @NestedConfigurationProperty private ProviderConfig provider = new ProviderConfig();
}
