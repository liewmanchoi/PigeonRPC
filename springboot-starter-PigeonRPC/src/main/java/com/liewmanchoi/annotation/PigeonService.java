package com.liewmanchoi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * @author wangsheng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface PigeonService {

  /** 服务提供者接口Class对象 */
  Class<?> interfaceClass() default void.class;

  /**
   * 调用超时时间
   */
  long timeout() default 3000;

  /**
   * 对外服务端口
   */
  int port() default 8000;

  /**
   * 业务线程池线程数量
   */
  int threadNum() default 10;
}
