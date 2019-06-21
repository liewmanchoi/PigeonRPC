package com.liewmanchoi.pigeon.rpc.serialization.protostuff;

import com.liewmanchoi.pigeon.rpc.serialization.api.ISerializer;
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
public class ProtoStuffSerializer implements ISerializer {
    private static Objenesis objenesis = new ObjenesisStd(true);

    @SuppressWarnings("unchecked")
    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            buffer.clear();
        }

    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            T message = objenesis.newInstance(clazz);
            Schema<T> schema = RuntimeSchema.getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
