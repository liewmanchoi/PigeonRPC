package com.liewmanchoi.pigeon.rpc.protocol.api.exporter.support;

import com.liewmanchoi.pigeon.rpc.config.ServiceConfig;
import com.liewmanchoi.pigeon.rpc.protocol.api.exporter.Exporter;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
public abstract class AbstractExporter<T> implements Exporter<T> {
    @Getter
    @Setter
    protected Invoker<T> invoker;

    @Getter
    @Setter
    protected ServiceConfig<T> serviceConfig;
}
