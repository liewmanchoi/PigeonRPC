package com.liewmanchoi.pigeon.rpc.utils;

import com.liewmanchoi.pigeon.rpc.serialization.support.SerializerType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author wangsheng
 * @date 2019/6/23
 */
@Slf4j
public class PropertyConfigure {
    private static final String PROPERTY_CLASSPATH = "/pigeon-config.properties";
    private static final Properties PROPERTIES = new Properties();
    /**
     * zookeeper服务地址
     */
    private static @Getter String  connectString;
    /**
     * 会话超时时间
     */
    private static @Getter int sessionTimeoutMs;
    /**
     * 连接创建超时时间
     */
    private static @Getter int connectionTimeoutMs;
    /**
     * 服务提供者的Netty连接数
     */
    private static @Getter int providerConnectionSize;
    /**
     * 序列化类型
     */
    private static @Getter SerializerType serializerType;

    static {
        try (InputStream inputStream = PropertyConfigure.class.getResourceAsStream(PROPERTY_CLASSPATH)) {
            if (inputStream == null) {
                throw new IllegalStateException("pigeon-config.properties cannot be found in the classpath.");
            }

            PROPERTIES.load(inputStream);
            connectString = PROPERTIES.getProperty("connectString");
            sessionTimeoutMs = Integer.parseInt(PROPERTIES.getProperty("sessionTimeoutMs", "60000"));
            connectionTimeoutMs = Integer.parseInt(PROPERTIES.getProperty("connectionTimeoutMs", "15000"));
            providerConnectionSize = Integer.parseInt(PROPERTIES.getProperty("providerConnectionSize", "10"));
            serializerType = SerializerType.queryByType(PROPERTIES.getProperty("serializerType", "ProtoStuffSerializer"));
        } catch (Throwable t) {
            log.warn("load pigeon.properties file failed", t);
            t.printStackTrace();
        }
    }
}
