package com.liewmanchoi.pigeon.rpc.common.enumeration;

import lombok.Getter;

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
    RECYCLER_ERROR("对象回收复用失败");

    private @Getter String errorCode;

    ErrorEnum(String errorCode) {
        this.errorCode = errorCode;
    }
}
