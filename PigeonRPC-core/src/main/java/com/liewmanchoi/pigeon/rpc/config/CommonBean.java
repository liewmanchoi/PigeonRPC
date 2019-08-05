package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.executor.api.PigeonExecutor;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.Protocol;
import com.liewmanchoi.pigeon.rpc.proxy.api.ProxyFactory;
import com.liewmanchoi.pigeon.rpc.registry.api.ServiceRegistry;
import com.liewmanchoi.pigeon.rpc.serialization.api.Serializer;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangsheng
 * @date 2019/8/3
 */
@Data
@NoArgsConstructor
@Builder
public class CommonBean {
  private Serializer serializer;
  private ProxyFactory proxyFactory;
  private ServiceRegistry serviceRegistry;
  private Protocol protocol;
  private PigeonExecutor serverExecutor;
}
