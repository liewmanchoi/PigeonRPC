package com.liewmanchoi.pigeon.rpc.common.domain;

import com.liewmanchoi.pigeon.rpc.common.utils.TypeUtil;
import io.netty.util.Recycler;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * RPC调用请求
 *
 * 使用场景：
 * <p>
 * 1) invoker发起RPC调用请求时创建，该对象序列化之后会被对象池回收 <br>
 * 2) provider收到请求时，反序列化之后创建，服务调用完毕之后由对象池回收
 * </p>
 *
 * @author wangsheng
 * @date 2019/6/22
 */
@Data
public class RPCRequest implements Serializable {
    private final transient Recycler.Handle<RPCRequest> handle;
    /**
     * 标识返回值ID
     */
    private String requestId;
    /**
     * 调用的接口名称
     */
    private String interfaceName;
    /**
     * 调用的方法名称
     */
    private String methodName;
    /**
     * 传递的方法参数
     */
    private Object[] args;
    /**
     * 传递的方法类型
     */
    private String[] argTypes;

    public RPCRequest(Recycler.Handle<RPCRequest> handle) {
        this.handle = handle;
    }

    public void setArgTypes(Class[] argTypes) {
        this.argTypes = new String[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
            this.argTypes[i] = argTypes[i].getName();
        }
    }

    public Class[] getArgTypes() throws ClassNotFoundException {
        Class[] argClasses = new Class[argTypes.length];

        for (int i = 0; i < argTypes.length; i++) {
            if (TypeUtil.isPrimitive(argTypes[i])) {
                // 如果属于基本数据类型
                argClasses[i] = TypeUtil.map(argTypes[i]);
            } else {
                // 如果不属于基本类型，则通过反射获取
                argClasses[i] = Class.forName(argTypes[i]);
            }
        }

        return argClasses;
    }

    public String key() {
        return interfaceName + "." +
                methodName + "." +
                Arrays.toString(argTypes) + "." +
                Arrays.toString(args);
    }

    public void recycle() {
        this.requestId = null;
        this.interfaceName = null;
        this.methodName = null;
        this.argTypes = null;
        this.args = null;
        handle.recycle(this);
    }
}
