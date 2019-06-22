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
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 5676151762752077199L;

    private String uniqueKey;
    private long invokeTimeout;
    private Object result;
}
