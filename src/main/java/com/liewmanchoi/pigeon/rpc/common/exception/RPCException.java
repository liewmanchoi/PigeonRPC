package com.liewmanchoi.pigeon.rpc.common.exception;

import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.utils.PlaceHolderUtil;
import lombok.Getter;

/**
 * @author wangsheng
 * @date 2019/6/26
 */
public class RPCException extends RuntimeException {
    @Getter
    private ErrorEnum errorEnum;

    public RPCException(ErrorEnum errorEnum, String message, Object... args) {
        super(PlaceHolderUtil.replace(message, args));
        this.errorEnum = errorEnum;
    }

    public RPCException(Throwable cause, ErrorEnum errorEnum, String message, Object... args) {
        super(PlaceHolderUtil.replace(message, args), cause);
        this.errorEnum = errorEnum;
    }
}
