package com.liewmanchoi.processor;

import com.liewmanchoi.annotation.PigeonService;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;

/**
 * @author wangsheng
 * @date 2019/7/12
 */
@Slf4j
public class ProviderBeanPostProcessor extends AbstractBeanPostProcessor {

  @Override
  @SuppressWarnings("unchecked")
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Class<?> beanClass = bean.getClass();
    if (!beanClass.isAnnotationPresent(PigeonService.class)) {
      return bean;
    }

    PigeonService pigeonService = beanClass.getAnnotation(PigeonService.class);
    Class<?> interfaceClass = pigeonService.interfaceClass();

    if (interfaceClass == void.class) {
      Class<?>[] interfaceClasses = beanClass.getInterfaces();

      if (interfaceClasses.length >= 1) {
        interfaceClass = interfaceClasses[0];
      } else {
        log.error("服务{}没有实现接口", beanClass);
        throw new RPCException(
            ErrorEnum.SERVICE_DIDNT_IMPLEMENT_INTERFACE, "服务{}没有实现接口", beanClass);
      }
    }

    ServiceConfig<?> config =
        ServiceConfig.builder()
            .interfaceClass((Class<Object>) interfaceClass)
            .reference(bean)
            .build();
    config.export();
    log.info("暴露服务[{}]", interfaceClass);
    return bean;
  }
}
