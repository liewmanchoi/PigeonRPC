package com.liewmanchoi.processor;

import com.liewmanchoi.annotation.PigeonReference;
import com.liewmanchoi.pigeon.rpc.common.ExtensionLoader;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import com.liewmanchoi.pigeon.rpc.filter.Filter;
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
        ReferenceConfig<?> config =
            ReferenceConfig.createReferenceConfig(
                interfaceClass.getName(),
                interfaceClass,
                reference.invokeMode(),
                reference.timeout(),
                false,
                ExtensionLoader.getInstance().load(Filter.class));
        initConfig(config);

        try {
          // 将被引用的服务对象替换为代理类对象
          field.set(bean, config.get());
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
