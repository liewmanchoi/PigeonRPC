package com.liewmanchoi.pigeon.rpc.common.enumeration;

import com.liewmanchoi.pigeon.rpc.common.enumeration.support.ExtensionBaseType;
import com.liewmanchoi.pigeon.rpc.serialization.api.Serializer;
import com.liewmanchoi.pigeon.rpc.serialization.protostuff.ProtoStuffSerializer;

/**
 * @author wangsheng
 */
public enum SerializerType implements ExtensionBaseType<Serializer> {
  /**
   * ProtoStuff
   */
  PROTOSTUFF(new ProtoStuffSerializer());

  private Serializer serializer;

  SerializerType(Serializer serializer) {
    this.serializer = serializer;
  }

  @Override
  public Serializer getInstance() {
    return serializer;
  }
}
