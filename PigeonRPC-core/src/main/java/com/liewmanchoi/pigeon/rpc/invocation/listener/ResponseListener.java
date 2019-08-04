package com.liewmanchoi.pigeon.rpc.invocation.listener;

/**
 * @author wangsheng
 * @date 2019/8/4
 */
public interface ResponseListener {
  void onComplete(Object result);

  void onError(Throwable throwable);
}
