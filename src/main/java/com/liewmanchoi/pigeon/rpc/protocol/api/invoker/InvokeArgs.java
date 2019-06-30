package com.liewmanchoi.pigeon.rpc.protocol.api.invoker;

/**
 * @author wangsheng
 * @date 2019/6/26
 */
public interface InvokeArgs {
    /**
     * getInterfaceName
     *
     * @return java.lang.String
     * @date 2019/6/26
     */
    String getInterfaceName();

    /**
     * getMethodName
     *
     * @return java.lang.String
     * @date 2019/6/26
     */
    String getMethodName();

    /**
     * getArgTypes
     *
     * @return java.lang.Class<?>[]
     * @date 2019/6/26
     */
    Class<?>[] getArgTypes();

    /**
     * getArgs
     *
     * @return java.lang.Object[]
     * @date 2019/6/26
     */
    Object[] getArgs();
    
    /**
     * getRequestId
     *
     * @return java.lang.String
     * @date 2019/6/26
     */
    String getRequestId();
}
