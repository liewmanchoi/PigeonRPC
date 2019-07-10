package com.liewmanchoi.pigeon.rpc.common;

import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/7/6
 */
@Slf4j
public class ExtensionLoader {
  private Map<String, Map<String, Object>> extensionMap = new HashMap<>();

  private ExtensionLoader() {}

  public static ExtensionLoader getInstance() {
    return SingletonHolder.INSTANCE;
  }

  public void loadResources() {
    URL url = getClass().getClassLoader().getResource("pigeon");

    if (url != null) {
      log.info("开始读取目录/pigeon下的配置文件...");
      File dir = new File(url.getFile());
      File[] files = dir.listFiles();
      if (files != null && files.length > 0) {
        for (File file : files) {
          processConfigFile(file);
        }

        return;
      }

      log.info("配置文件目录/pigeon下没有配置文件");
      return;
    }
    log.info("配置文件目录/pigeon不存在");
  }

  private void processConfigFile(File file) {
    log.info("开始读取文件[{}]", file);

    String fileName = file.getName();

    try {
      Class<?> interfaceClass = Class.forName(fileName);

      try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          String[] kv = line.split(":");

          if (kv.length != 2) {
            log.error("配置文件[{}]格式错误，正确格式为'x = y'，错误内容为[{}]", fileName, line);
            throw new RPCException(ErrorEnum.EXTENSION_CONFIG_FILE_ERROR, "配置文件内容格式错误");
          }

          try {
            Class<?> impl = Class.forName(kv[1]);
            if (!interfaceClass.isAssignableFrom(impl)) {
              log.error("实现类[{}]不是接口[{}]的子类", impl, interfaceClass);
              throw new RPCException(
                  ErrorEnum.EXTENSION_CONFIG_FILE_ERROR, "实现类{}不是该接口{}的子类", impl, interfaceClass);
            }

            Object object = impl.getConstructor().newInstance();
            register(interfaceClass, kv[0], object);

          } catch (ClassNotFoundException e) {
            log.error("无法加载类[{}]", kv[1]);
            throw new RPCException(ErrorEnum.EXTENSION_CONFIG_FILE_ERROR, "无法加载类[{}]", kv[1]);
          } catch (InstantiationException
              | InvocationTargetException
              | NoSuchMethodException
              | IllegalAccessException e) {
            log.error("类[{}]无法使用无参构造函数进行实例化", kv[1]);
            throw new RPCException(
                ErrorEnum.EXTENSION_CONFIG_FILE_ERROR, "类[{}]无法使用无参构造函数进行实例化", kv[1]);
          }
        }
      } catch (IOException e) {
        log.error("文件[{}]读取失败，异常[{}]", fileName, e);
        throw new RPCException(ErrorEnum.EXTENSION_CONFIG_FILE_ERROR, "文件[{}]读取失败", fileName);
      }
    } catch (ClassNotFoundException e) {
      log.error("找不到类[{}]，异常[{}]", fileName, e);
      throw new RPCException(ErrorEnum.EXTENSION_CONFIG_FILE_ERROR, "接口对象[{}]加载类失败", fileName);
    }
  }

  public <T> List<T> load(Class<T> interfaceClass) {
    String interfaceName = interfaceClass.getName();
    if (!extensionMap.containsKey(interfaceName)) {
      return Collections.emptyList();
    }

    Collection<Object> objects = extensionMap.get(interfaceName).values();
    List<T> instances = new ArrayList<>();

    objects.forEach(o -> instances.add(interfaceClass.cast(o)));
    return instances;
  }

  public <T, K extends Enum<K>> T load(Class<T> interfaceClass, Class<K> enumType, String name) {
    //    ExtensionBaseType<T> extensionBaseType = (ExtensionBaseType<T>)
    // ExtensionBaseType.valueOf(enumType, name.toUpperCase());

    String interfaceName = interfaceClass.getName();
    if (!extensionMap.containsKey(interfaceName)
        || !extensionMap.get(interfaceName).containsKey(name)) {
      log.error("接口[{}]没有可用的实现类[{}]", interfaceName, name);
      throw new RPCException(
          ErrorEnum.NO_SUPPORTED_INSTANCE, "接口[{}]没有可用的实现类[{}]", interfaceName, name);
    }

    Object object = extensionMap.get(interfaceName).get(name);
    return interfaceClass.cast(object);
  }

  public void register(Class<?> interfaceClass, String alias, Object instance) {
    String interfaceName = interfaceClass.getName();

    extensionMap.computeIfAbsent(interfaceName, v -> new HashMap<>(16));
    log.info("注册Bean: interface[{}], alias[{}], instance[{}]", interfaceName, alias, instance);
    extensionMap.get(interfaceName).put(alias, instance);
  }

  private static class SingletonHolder {
    private static final ExtensionLoader INSTANCE = new ExtensionLoader();
  }
}
