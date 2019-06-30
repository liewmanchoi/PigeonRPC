package com.liewmanchoi.pigeon.rpc.common.domain;

import com.liewmanchoi.pigeon.rpc.config.ReferenceConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangsheng
 * @date 2019/6/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RPCRequestWrapper {
    private ReferenceConfig<?> referenceConfig;
    private RPCRequest rpcRequest;
    /**
     * getInterfaceName
     *
     * @return java.lang.String
     * @date 2019/6/26
     */
    String getInterfaceName() {
        return rpcRequest.getInterfaceName();
    }

    /**
     * getMethodName
     *
     * @return java.lang.String
     * @date 2019/6/26
     */
    String getMethodName() {
        return rpcRequest.getMethodName();
    }

    /**
     * getArgTypes
     *
     * @return java.lang.Class<?>[]
     * @date 2019/6/26
     */
    Class<?>[] getArgTypes() {
        return rpcRequest.getArgTypes();
    }

    /**
     * getArgs
     *
     * @return java.lang.Object[]
     * @date 2019/6/26
     */
    Object[] getArgs() {
        return rpcRequest.getArgs();
    }
    
    /**
     * getRequestId
     *
     * @return java.lang.String
     * @date 2019/6/26
     */
    String getRequestId() {
        return rpcRequest.getRequestId();
    }
}
