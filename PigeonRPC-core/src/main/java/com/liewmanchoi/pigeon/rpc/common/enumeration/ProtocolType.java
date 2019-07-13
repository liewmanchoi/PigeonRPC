package com.liewmanchoi.pigeon.rpc.common.enumeration;

import com.liewmanchoi.pigeon.rpc.common.enumeration.support.ExtensionBaseType;
import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.Protocol;
import com.liewmanchoi.pigeon.rpc.protocol.pigeon.PigeonProtocol;

/**
 * @author wangsheng
 * @date 2019/7/13
 */
public enum ProtocolType implements ExtensionBaseType<Protocol> {
  /**
   * PIGEON
   */
  PIGEON(new PigeonProtocol());

  private Protocol protocol;

  ProtocolType(Protocol protocol) {
    this.protocol = protocol;
  }

  @Override
  public Protocol getInstance() {
    return protocol;
  }
}
