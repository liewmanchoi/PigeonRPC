package com.liewmanchoi.pigeon.rpc.transport.api;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author wangsheng
 * @date 2019/6/26
 */
public interface Server {

  /**
   * 启动
   */
  void start();

  /**
   * 处理RPC调用请求
   *
   * @param request 请求体
   * @param ctx ChannelHandlerContext
   * @date 2019/6/26
   */
  void handleRPCRequest(RPCRequest request, ChannelHandlerContext ctx);

  /**
   * 停机
   *
   * @date 2019/6/26
   */
  void close();
}
