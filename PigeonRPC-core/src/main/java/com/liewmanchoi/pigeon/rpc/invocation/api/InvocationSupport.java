package com.liewmanchoi.pigeon.rpc.invocation.api;

import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.enumeration.InvokeMode;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.invocation.async.AsyncInvocation;
import com.liewmanchoi.pigeon.rpc.invocation.oneway.OneWayInvocation;
import com.liewmanchoi.pigeon.rpc.invocation.sync.SyncInvocation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/7/1
 */
@Slf4j
public class InvocationSupport {

  public static Invocation getInvocation(InvokeMode invokeMode) {
    switch (invokeMode) {
      case SYNC:
        return SyncHolder.instance;
      case ASYNC:
        return AsyncHolder.instance;
      case ONEWAY:
        return OnewayHolder.instance;
      default:
        log.error("非法调用类型参数[{}]", invokeMode);
        throw new RPCException(ErrorEnum.ILLEGAL_INVOCATION_TYPE, "非法调用类型", invokeMode);
    }
  }

  private static class OnewayHolder {

    private static OneWayInvocation instance = new OneWayInvocation();
  }

  private static class AsyncHolder {

    private static AsyncInvocation instance = new AsyncInvocation();
  }

  private static class SyncHolder {

    private static SyncInvocation instance = new SyncInvocation();
  }
}
