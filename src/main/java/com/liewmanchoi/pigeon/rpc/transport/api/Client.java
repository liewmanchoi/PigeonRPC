package com.liewmanchoi.pigeon.rpc.transport.api;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Future;

/**
 * @author wangsheng
 * @date 2019/6/26
 */
public interface Client {
    /**
     * 发起远程调用
     * @param request 调用请求
     * @return Future<RPCResponse> 异步调用返回结果
     */
    Future<RPCResponse> submit(RPCRequest request);

    /**
     * 关闭
     */
    void close();
    /**
     * 获取服务注册地址ServiceURL
     *
     * @return com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL
     * @date 2019/6/26
     */
    ServiceURL getServiceURL();

    /**
     * 处理异常
     * @param throwable 异常
     */
    void handleException(Throwable throwable);
    /**
     * 处理回调请求
     *
     * @param request server端发送的回调请求
     * @param ctx ChannelHandlerContext
     * @date 2019/6/26
     */
    void handleCallbackRequest(RPCRequest request, ChannelHandlerContext ctx);
    /**
     * 处理调用返回结果RPCResponse
     *
     * @param [response]
     * @date 2019/6/26
     */
    void handleRPCResponse(RPCResponse response);
    /**
     * TODO:更新注释
     *
     * @param []
     * @return boolean
     * @date 2019/6/26
     */
    boolean isAvailable();
    /**
     * 更新服务注册ServiceURL
     *
     * @param [serviceURL]
     * @date 2019/6/26
     */
    void updateServiceConfig(ServiceURL serviceURL);
}
