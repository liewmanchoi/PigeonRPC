package com.liewmanchoi.pigeon.rpc.common.enumeration;

import lombok.Getter;
import org.omg.CORBA.UNKNOWN;

/**
 * @author wangsheng
 */
public enum ErrorEnum {
    /**
     * 序列化故障
     */
    SERIALIZATION_ERROR("序列化故障"),
    /**
     * 自定义RPCException消息模板替换出错
     */
    TEMPLATE_REPLACEMENT_ERROR("自定义RPCException消息模板替换出错"),
    /**
     * 对象回收复用失败
     */
    RECYCLER_ERROR("对象回收复用失败"),
    /**
     * localhost解析失败
     */
    UNKNOWN_HOST_EXCEPTION("localhost解析失败"),
    /**
     * 客户端无法建立到服务端的连接
     */
    CONNECT_TO_SERVER_FAILURE("客户端无法建立到服务端的连接"),
    /**
     * Endpoint关闭后仍在提交任务
     */
    SUBMIT_AFTER_ENDPOINT_CLOSED("Endpoint关闭后仍在提交任务");

    private @Getter String errorCode;

    ErrorEnum(String errorCode) {
        this.errorCode = errorCode;
    }
}
