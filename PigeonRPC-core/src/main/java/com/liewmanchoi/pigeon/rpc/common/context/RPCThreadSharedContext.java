package com.liewmanchoi.pigeon.rpc.common.context;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.config.ServiceConfig;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangsheng
 * @date 2019/6/27
 */
public class RPCThreadSharedContext {
    private static final Map<String, CompletableFuture<RPCResponse>> RESPONSES_MAP = new ConcurrentHashMap<>();
    private static final Map<String, ServiceConfig<?>> HANDLERS_MAP = new ConcurrentHashMap<>();

    public static void registerResponseFuture(String requestId,
                                              CompletableFuture<RPCResponse> responseCompletableFuture) {
        RESPONSES_MAP.put(requestId, responseCompletableFuture);
    }

    public static CompletableFuture<RPCResponse> getAndRemoveResponseFuture(String requestId) {
        return RESPONSES_MAP.remove(requestId);
    }

    public static <T> void registerHandler(String name, ServiceConfig<T> serviceConfig) {
        HANDLERS_MAP.put(name, serviceConfig);
    }

    public static ServiceConfig<?> getAndRemoveHandler(String name) {
        return HANDLERS_MAP.remove(name);
    }
}
