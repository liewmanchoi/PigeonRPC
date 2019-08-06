package com.liewmanchoi.pigeon.rpc.common.context;

import com.liewmanchoi.pigeon.rpc.invocation.future.ResponseFuture;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import lombok.Getter;
import lombok.Setter;

/**
 * 线程私有上下文，用于存储归属于本线程的future和invoker
 *
 * @author wangsheng
 * @date 2019/7/1
 */
public class RpcContext {
  private static ThreadLocal<RpcContext> DEFAULT_CONTEXT = ThreadLocal.withInitial(RpcContext::new);
  @Getter @Setter private ResponseFuture future;
  @Getter @Setter private Invoker<?> invoker;

  private RpcContext() {}

  public static RpcContext getContext() {
    return DEFAULT_CONTEXT.get();
  }
}
