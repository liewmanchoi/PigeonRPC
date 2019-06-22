package com.liewmanchoi.pigeon.rpc.protocol;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wangsheng
 * @date 2019/6/22
 */
@Getter
@Setter
@NoArgsConstructor
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -6156262072057306469L;
    /**
     * 标识返回值ID
     */
    private String uniqueKey;
    /**
     * 服务提供者信息
     */
    private ProviderService providerService;
    /**
     * 调用的方法名称
     */
    private String invokedMethodName;
    /**
     * 传递的方法参数
     */
    private Object[] args;
    /**
     * 消费端应用名称
     */
    private String appName;
    /**
     * 消费请求超时时长
     */
    private long invokeTimeout;
}
