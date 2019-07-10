package com.liewmanchoi.pigeon.rpc.common.domain;

import io.netty.util.Recycler;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

/**
 * RPC调用响应
 *
 * <p>使用场景： 1) provider调用完服务后创建该对象，经过序列化之后传递给invoker，然后被对象池回收 <br>
 * 2) invoker收到响应，进行反序列化后创建该对象，返回给用户之前被对象池回收
 *
 * @author wangsheng
 * @date 2019/6/22
 */
@Data
@ToString
public class RPCResponse implements Serializable {
  private final transient Recycler.Handle<RPCResponse> handle;
  private String requestId;
  private Throwable cause;
  private Object result;

  public RPCResponse(Recycler.Handle<RPCResponse> handle) {
    this.handle = handle;
  }

  public boolean hasError() {
    return cause != null;
  }

  public void recycle() {
    requestId = null;
    cause = null;
    result = null;

    handle.recycle(this);
  }
}
