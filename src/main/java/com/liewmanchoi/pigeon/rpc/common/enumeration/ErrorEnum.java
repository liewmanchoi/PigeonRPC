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
    SUBMIT_AFTER_ENDPOINT_CLOSED("Endpoint关闭后仍在提交任务"),
    /**
     * 超过心跳超时时间
     */
    HEART_BEAT_TIME_OUT_EXCEED("超过心跳超时时间"),
    /**
     * 获取本地Host失败
     */
    READ_LOCALHOST_ERROR("读取本地Host失败"),
    /**
     * 调用失败
     */
    INVOKE_FAILURE("调用失败"),
    /**
     * 非常调用类型
     */
    ILLEGAL_INVOCATION_TYPE("非法调用类型"),
    /**
     * 无法查找到可用的服务提供者
     */
    NO_SERVER_AVAILABLE("无法查找到可用的服务提供者"),
    /**
     * 服务调用失败
     */
    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    /**
     * 无法连接到Zookeeper集群
     */
    FAILED_CONNECT_ZOOKEEPER("无法连接到Zookeeper集群"),
    /**
     * 服务注册失败
     */
    SERVICE_REGISTER_FAILURE("服务注册失败"),
    /**
     * 服务发现故障
     */
    SERVICE_DISCOVER_FAILURE("服务发现故障");

    private @Getter String errorCode;

    ErrorEnum(String errorCode) {
        this.errorCode = errorCode;
    }
}
