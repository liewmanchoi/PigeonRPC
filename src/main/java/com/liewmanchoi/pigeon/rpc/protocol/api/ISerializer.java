package com.liewmanchoi.pigeon.rpc.protocol.api;

/**
 * @author wangsheng
 * @date 2019/6/21
 */
public interface ISerializer {
    /**
     * serialize
     *
     * @param obj 待序列化对象
     * @return byte[]
     * @date 2019/6/21
     */
    <T> byte[] serialize(T obj);

    /**
     * deserialize
     *
     * @param data 字节流
     * @param clazz 类对应的Class对象
     * @return T
     * @date 2019/6/21
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
