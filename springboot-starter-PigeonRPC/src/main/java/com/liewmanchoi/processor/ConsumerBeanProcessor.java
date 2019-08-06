package com.liewmanchoi.processor;

import com.liewmanchoi.annotation.PigeonReference;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.enumeration.FaultToleranceType;
import com.liewmanchoi.pigeon.rpc.common.enumeration.LoadBalancerType;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.ConsumerBean;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;

/**
 * @author wangsheng
 * @date 2019/7/11
 */
@Slf4j
public class ConsumerBeanProcessor extends AbstractBeanPostProcessor {
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Class<?> beanClass = bean.getClass();
    Field[] fields = beanClass.getDeclaredFields();

    for (Field field : fields) {
      if (!field.isAccessible()) {
        field.setAccessible(true);
      }

      Class<?> interfaceClass = field.getType();
      PigeonReference reference = field.getAnnotation(PigeonReference.class);

      if (reference != null) {
        // 去除PigeonReference注解中的各种属性
        boolean oneway = reference.oneway();
        boolean async = reference.async();
        LoadBalancerType loadBalancerType = reference.loadBalancer();
        FaultToleranceType faultToleranceType = reference.faultTolerance();

        ConsumerBean<?> consumerBean =
            ConsumerBean.createConsumerBean(
                interfaceClass, oneway, async, loadBalancerType, faultToleranceType);

        initCommon(consumerBean);

        try {
          // 将被引用的服务对象替换为代理类对象
          field.set(bean, consumerBean.getProxyBean());
        } catch (IllegalAccessException e) {
          log.error("无法使用反射将类成员替换成为代理类对象", e);
          throw new RPCException(e, ErrorEnum.AUTOWIRE_REFERENCE_PROXY_ERROR, "注册proxy实例失败");
        }

        log.info("注入依赖[{}]", interfaceClass);
      }
    }

    return null;
  }
}
