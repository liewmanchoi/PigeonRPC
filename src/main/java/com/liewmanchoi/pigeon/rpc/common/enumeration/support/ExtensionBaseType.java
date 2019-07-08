package com.liewmanchoi.pigeon.rpc.common.enumeration.support;

import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;

/**
 * @author wangsheng
 * @date 2019/7/7
 */
public interface ExtensionBaseType<T> {
  static <T extends Enum<T>> ExtensionBaseType<?> valueOf(Class<T> enumType, String name) {
    Enum<?> anEnum = Enum.valueOf(enumType, name);

    if (anEnum instanceof ExtensionBaseType) {
      return (ExtensionBaseType<?>) anEnum;
    }

    throw new RPCException(
        ErrorEnum.ENUM_DIDNT_EXTENDS_EXTENSION_BASE_TYPE,
        "枚举类[{}]没有实现ExtensionBaseType接口",
        enumType);
  }

  /**
   * 获取对象实例
   *
   * @return T
   */
  T getInstance();
}
