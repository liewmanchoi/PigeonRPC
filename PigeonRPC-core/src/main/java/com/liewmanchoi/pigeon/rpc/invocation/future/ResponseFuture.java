package com.liewmanchoi.pigeon.rpc.invocation.future;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.invocation.listener.ResponseListener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author wangsheng
 * @date 2019/8/4
 */
public class ResponseFuture implements Future<RPCResponse> {
  /** 使用组合方式包含CompletableFuture */
  private CompletableFuture<RPCResponse> completableFuture = new CompletableFuture<>();

  public void complete(RPCResponse response) {
    completableFuture.complete(response);
  }

  /** 添加回调方法 */
  public void addListener(ResponseListener listener) {
    completableFuture.thenAccept(
        response -> {
          if (response.hasError()) {
            // 如果返回结果有误
            Throwable throwable = response.getCause();
            // 执行onError方法
            listener.onError(throwable);
            return;
          }

          // 正常结果，则执行onComplete方法
          listener.onComplete(response.getResult());
        });
  }

  public void addListener(ResponseListener listener, Executor executor) {
    completableFuture.thenAcceptAsync(
        response -> {
          if (response.hasError()) {
            // 如果返回结果有误
            Throwable throwable = response.getCause();
            // 执行onError方法
            listener.onError(throwable);
            return;
          }

          // 正常结果，则执行onComplete方法
          listener.onComplete(response.getResult());
        },
        executor);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return completableFuture.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return completableFuture.isCancelled();
  }

  @Override
  public boolean isDone() {
    return completableFuture.isDone();
  }

  @Override
  public RPCResponse get() throws InterruptedException, ExecutionException {
    return completableFuture.get();
  }

  @Override
  public RPCResponse get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return completableFuture.get(timeout, unit);
  }
}
