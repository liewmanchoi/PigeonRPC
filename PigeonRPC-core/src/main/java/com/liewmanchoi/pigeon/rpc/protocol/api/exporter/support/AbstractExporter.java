package com.liewmanchoi.pigeon.rpc.protocol.api.exporter.support;

import com.liewmanchoi.pigeon.rpc.config.ProviderBean;
import com.liewmanchoi.pigeon.rpc.protocol.api.exporter.Exporter;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
@Getter
@Setter
public abstract class AbstractExporter<T> implements Exporter<T> {
  protected Invoker<T> invoker;
  protected ProviderBean<T> providerBean;
}
