package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.proxy.api.ProxyFactory;
import com.liewmanchoi.pigeon.rpc.serialization.api.Serializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangsheng
 * @date 2019/7/2
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationConfig {
    private String name;
    private String serialize;
    private String proxy;

    private Serializer serializerInstance;
    private ProxyFactory proxyFactoryInstance;
}
