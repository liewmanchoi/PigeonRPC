package com.liewmanchoi.pigeon.rpc.protocol.api.protocol.support;

import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.GlobalConfig;
import com.liewmanchoi.pigeon.rpc.config.ServiceConfig;
import com.liewmanchoi.pigeon.rpc.protocol.api.exporter.Exporter;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.Protocol;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
@Slf4j
public abstract class AbstractProtocol implements Protocol {
    /**
     * 关于Exporter的缓存，键为接口名称，值为Exporter对象
     */
    private Map<String, Exporter<?>> exporterMap = new ConcurrentHashMap<>();
    @Getter
    private GlobalConfig globalConfig;

    public void init(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public <T> void putExporter(Class<?> interfaceClazz, Exporter<T> exporter) {
        exporterMap.put(interfaceClazz.getName(), exporter);
    }

    @Override
    public ServiceConfig<?> referLocalService(String interfaceName) throws RPCException {
        if (exporterMap.containsKey(interfaceName)) {
            log.error("无法找到服务-[{}]，服务可能没有发布", interfaceName);
            return null;
        }

        return exporterMap.get(interfaceName).getServiceConfig();
    }
}
