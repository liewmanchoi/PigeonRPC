package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.common.enumeration.InvokeMode;
import com.liewmanchoi.pigeon.rpc.filter.Filter;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    /**
     * interfaceName -> referenceConfig
     */
    private static final Map<String, ReferenceConfig<?>> REFERENCE_CONFIG_MAP = new ConcurrentHashMap<>();

    private Class<T> interfaceClass;
    private long timeout;
    private InvokeMode invokeMode;
    private List<Filter> filters;

    public static ReferenceConfig<?> getConfigByInterfaceName(String interfaceName) {
        return REFERENCE_CONFIG_MAP.get(interfaceName);
    }
}
