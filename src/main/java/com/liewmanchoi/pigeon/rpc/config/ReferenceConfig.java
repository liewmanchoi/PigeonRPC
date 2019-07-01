package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.common.enumeration.InvokeMode;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO: ReferenceConfig
 * 引用服务配置
 *
 * @author wangsheng
 * @date 2019/6/30
 */
@Data
@Builder
@Slf4j
public class ReferenceConfig<T> {
    private long timeout;
    private InvokeMode invokeMode;
}
