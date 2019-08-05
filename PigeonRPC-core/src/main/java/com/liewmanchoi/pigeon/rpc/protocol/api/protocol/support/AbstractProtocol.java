package com.liewmanchoi.pigeon.rpc.protocol.api.protocol.support;

import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.config.CommonBean;
import com.liewmanchoi.pigeon.rpc.config.ServiceConfig;
import com.liewmanchoi.pigeon.rpc.protocol.api.exporter.Exporter;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.Protocol;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/6/30
 */
@Slf4j
public abstract class AbstractProtocol implements Protocol {

  protected CommonBean commonBean;
  protected int port;
  /** 关于Exporter的缓存，键为接口名称，值为Exporter对象 */
  private Map<String, Exporter<?>> exporterMap = new ConcurrentHashMap<>();

  public void init(CommonBean commonBean, int port) {
    this.commonBean = commonBean;
    this.port = port;
  }

  protected <T> void putExporter(Class<?> interfaceClazz, Exporter<T> exporter) {
    exporterMap.put(interfaceClazz.getName(), exporter);
  }

  @Override
  public ServiceConfig<?> referLocalService(String interfaceName) throws RPCException {
    if (exporterMap.containsKey(interfaceName)) {
      log.warn("无法找到服务[{}]，服务可能没有发布", interfaceName);
      return null;
    }

    return exporterMap.get(interfaceName).getServiceConfig();
  }
}
