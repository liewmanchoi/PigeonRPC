package com.liewmanchoi.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangsheng
 * @date 2019/8/3
 */
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "pigeon.provider")
public class ProviderProperties {
  private Integer port;
  private Integer workerThreads;
}
