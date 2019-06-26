package com.liewmanchoi.pigeon.rpc.protocol.api;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL;

/**
 * @author wangsheng
 * @date 2019/6/26
 */
public interface Invoker<T> {
    /**
     * getInterface
     *
     * @return java.lang.Class<T>
     * @date 2019/6/26
     */
    Class<T> getInterface();

    /**
     * getInterfaceName
     *
     * @return java.lang.String
     * @date 2019/6/26
     */
    String getInterfaceName();

    /**
     * invoke
     *
     * @param invokeArgs 调用参数
     * @return com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse
     * @exception RPCException 自定义异常
     * @date 2019/6/26
     */
    RPCResponse invoke(InvokeArgs invokeArgs) throws RPCException;

    /**
     * 返回注册地址
     *
     * 本地服务返回本地IP地址 <br>
     * 远程服务返回注册中心的ServiceURL
     *
     * @return com.liewmanchoi.pigeon.rpc.registry.api.ServiceURL
     * @date 2019/6/26
     */
    ServiceURL getServiceURL();
}
