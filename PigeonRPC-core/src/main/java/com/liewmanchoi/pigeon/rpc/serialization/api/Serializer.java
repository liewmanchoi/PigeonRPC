package com.liewmanchoi.pigeon.rpc.serialization.api;

import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;

/**
 * @author wangsheng
 * @date 2019/6/21
 */
public interface Serializer {

  /**
   * serialize
   *
   * @param obj 待序列化对象
   * @return byte[]
   * @throws RPCException 异常
   * @date 2019/6/21
   */
  <T> byte[] serialize(T obj) throws RPCException;

  /**
   * deserialize
   *
   * @param data 字节流
   * @param clazz 类对应的Class对象
   * @return T
   * @date 2019/6/21
   */
  <T> T deserialize(byte[] data, Class<T> clazz) throws RPCException;
}
