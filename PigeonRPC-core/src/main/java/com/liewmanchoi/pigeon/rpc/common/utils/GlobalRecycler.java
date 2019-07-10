package com.liewmanchoi.pigeon.rpc.common.utils;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import io.netty.util.Recycler;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于Recycler的对象池缓存器
 *
 * @author wangsheng
 * @date 2019/6/26
 */
public class GlobalRecycler {

  private static Map<Class<?>, Recycler<?>> CACHE_MAP = new HashMap<>();

  static {
    CACHE_MAP.put(
        RPCRequest.class,
        new Recycler<RPCRequest>() {
          @Override
          protected RPCRequest newObject(Handle<RPCRequest> handle) {
            return new RPCRequest(handle);
          }
        });

    CACHE_MAP.put(
        RPCResponse.class,
        new Recycler<RPCResponse>() {
          @Override
          protected RPCResponse newObject(Handle<RPCResponse> handle) {
            return new RPCResponse(handle);
          }
        });
  }

  public static boolean isRecyclable(Class<?> clazz) {
    return CACHE_MAP.containsKey(clazz);
  }

  @SuppressWarnings("unchecked")
  public static <T> T reuse(Class<T> clazz) {
    if (isRecyclable(clazz)) {
      return (T) CACHE_MAP.get(clazz).get();
    }

    throw new RPCException(ErrorEnum.RECYCLER_ERROR, "该类型对象不可回收复用: {}", clazz);
  }
}
