package com.liewmanchoi.pigeon.rpc.serialization.support;


/**
 * @author liewmanchoi
 */
public enum SerializerType {
    /**
     * ProStuffSerializer ProStuff序列化类
     */
    ProtoStuffSerializer("ProtoStuffSerializer");

    private String serializerType;

    SerializerType(String serializerType) {
        this.serializerType = serializerType;
    }

    public static SerializerType queryByType(String serializerType) {
        if (serializerType == null || serializerType.length() == 0) {
            return null;
        }

        for (SerializerType type : SerializerType.values()) {
            if (serializerType.equals(type.getTypeString())) {
                return type;
            }
        }

        return null;
    }

    public String getTypeString() {
        return serializerType;
    }
}
