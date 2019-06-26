package com.liewmanchoi.pigeon.rpc.serialization.protostuff;

import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.common.utils.GlobalRecycler;
import com.liewmanchoi.pigeon.rpc.serialization.api.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * 基于Protostuff的序列化与反序列化
 * @author wangsheng
 */
public class ProtoStuffSerializer implements Serializer {
    private static class ObjenesisHolder {
        // 使用静态类使用懒加载
        static Objenesis objenesis = new ObjenesisStd(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> byte[] serialize(T obj) throws RPCException {
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Throwable throwable) {
            throw new RPCException(ErrorEnum.SERIALIZATION_ERROR, "对象: {} 序列化失败: {}", obj, throwable);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws RPCException {
        try {
            T object;
            if (GlobalRecycler.isRecyclable(clazz)) {
                object = GlobalRecycler.reuse(clazz);
            } else {
                object = ObjenesisHolder.objenesis.newInstance(clazz);
                Schema<T> schema = RuntimeSchema.getSchema(clazz);
                ProtostuffIOUtil.mergeFrom(data, object, schema);
            }
            return object;
        } catch (Throwable throwable) {
            throw new RPCException(ErrorEnum.SERIALIZATION_ERROR, "字节流反序列化失败: {}", throwable);
        }
    }
}
