package com.liewmanchoi.pigeon.rpc.protocol;

import java.io.Serializable;
import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author wangsheng
 * @date 2019/6/22
 */
@Getter
@Setter
@NoArgsConstructor
public class InvokerInfo implements Serializable {

  private static final long serialVersionUID = 4948885607772397607L;

  private Class<?> serviceInterface;
  private Object serviceObject;
  private Method serviceMethod;
  private String invokerIp;
  private int invokerPort;
  private long timeout;
  private String remoteAppKey;
  private String groupName = "default";
}
