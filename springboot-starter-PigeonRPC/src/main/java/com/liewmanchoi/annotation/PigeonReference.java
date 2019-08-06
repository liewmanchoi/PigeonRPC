package com.liewmanchoi.annotation;

import com.liewmanchoi.pigeon.rpc.common.enumeration.FaultToleranceType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.LoadBalancerType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务引用注解
 *
 * @author wangsheng
 * @date 2019/7/11
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PigeonReference {
  //  InvokeType invokeMode() default InvokeType.ASYNC;

  /** 服务提供者接口Class对象 */
  Class<?> interfaceClass() default void.class;

  /** 是否关心返回值 */
  boolean oneway() default false;

  /** 是否异步调用 */
  boolean async() default false;

  /** 调用超时 */
  long timeout() default 3000L;

  /** 负载均衡方式 */
  LoadBalancerType loadBalancer() default LoadBalancerType.RANDOM;

  /** 容错模式 */
  FaultToleranceType faultTolerance() default FaultToleranceType.FAIL_SAFE;
}
