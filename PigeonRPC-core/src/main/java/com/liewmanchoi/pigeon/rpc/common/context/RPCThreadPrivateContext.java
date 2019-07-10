package com.liewmanchoi.pigeon.rpc.common.context;

import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import java.util.concurrent.Future;
import lombok.Getter;
import lombok.Setter;

/**
 * 线程私有上下文，用于存储归属于本线程的future和invoker
 *
 * @author wangsheng
 * @date 2019/7/1
 */
public class RPCThreadPrivateContext {

    private static ThreadLocal<RPCThreadPrivateContext> DEFAULT_CONTEXT =
        ThreadLocal.withInitial(RPCThreadPrivateContext::new);
    @Getter
    @Setter
    private Future<?> future;
    @Getter
    @Setter
    private Invoker<?> invoker;

    private RPCThreadPrivateContext() {
    }

    public static RPCThreadPrivateContext getContext() {
        return DEFAULT_CONTEXT.get();
    }
}
