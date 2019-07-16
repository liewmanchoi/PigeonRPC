package com.liewmanchoi.pigeon.rpc.common.annotation;

import com.liewmanchoi.pigeon.rpc.common.enumeration.InvokeType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.LoadBalancerType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.SerializerType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wangsheng
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PigeonReference {

  /**
   * 序列化方式
   */
  SerializerType serializerType() default SerializerType.PROTOSTUFF;

  /**
   * 调用方式
   */
  InvokeType invokeType() default InvokeType.SYNC;

  /**
   * 软负载均衡策略
   */
  LoadBalancerType loadBalancerType() default LoadBalancerType.RANDOM;

  /**
   * 版本号
   */
  String version() default "";

  /**
   * 超时时间设置
   */
  long timeout() default 1000L;
}
