package com.liewmanchoi.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangsheng
 * @date 2019/8/3
 */
@Data
@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(prefix = "pigeon.protocol")
public class ProtocolProperties {
  private String proxy;
  private String serializer;
}
