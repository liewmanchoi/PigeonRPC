package com.liewmanchoi.pigeon.rpc.common.context;

import com.liewmanchoi.pigeon.rpc.invocation.future.ResponseFuture;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangsheng
 * @date 2019/6/27
 */
public class RpcSharedContext {
  private static final Map<String, ResponseFuture> RESPONSES_MAP = new ConcurrentHashMap<>();

  public static void registerResponseFuture(String requestId, ResponseFuture future) {
    RESPONSES_MAP.put(requestId, future);
  }

  public static ResponseFuture getAndRemoveResponseFuture(String requestId) {
    return RESPONSES_MAP.remove(requestId);
  }
}
