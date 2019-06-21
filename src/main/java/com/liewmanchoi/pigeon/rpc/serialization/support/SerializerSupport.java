package com.liewmanchoi.pigeon.rpc.serialization.support;

import com.liewmanchoi.pigeon.rpc.serialization.api.ISerializer;
import com.liewmanchoi.pigeon.rpc.serialization.protostuff.ProtoStuffSerializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangsheng
 * @date 2019/6/21
 */
public class SerializerSupport {
    private static final Map<SerializerType, ISerializer> REGISTRY_MAP = new ConcurrentHashMap<>();
    static {
        REGISTRY_MAP.put(SerializerType.ProtoStuffSerializer, new ProtoStuffSerializer());
    }

    public static <T> byte[] serialize(T obj, String typeString) {
        SerializerType type = SerializerType.queryByType(typeString);
        if (type == null) {
            throw new RuntimeException("serialization type is null");
        }

        ISerializer serializer = REGISTRY_MAP.get(type);
        if (serializer == null) {
            throw new RuntimeException("serialization error");
        }

        try {
            return serializer.serialize(obj);
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> clazz, String typeString) {
        SerializerType type = SerializerType.queryByType(typeString);
        if (type == null) {
            throw new RuntimeException("serialization type is null");
        }

        ISerializer serializer = REGISTRY_MAP.get(type);
        if (serializer == null) {
            throw new RuntimeException("serialization error");
        }

        try {
            return serializer.deserialize(data, clazz);
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }
}
