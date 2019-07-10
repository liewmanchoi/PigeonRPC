package com.liewmanchoi.pigeon.rpc.protocol;

import java.io.Serializable;
import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 服务提供者的注册信息
 *
 * @author wangsheng
 * @date 2019/6/22
 */
@Getter
@Setter
@NoArgsConstructor
public class ProviderInfo implements Serializable {

  private static final long serialVersionUID = 7686433457110952588L;
  /**
   * 服务接口，服务调用端获取后缓存在本地，用于发起调用服务
   */
  private Class<?> serviceInterface;
  /**
   * 服务实现类对象
   */
  private transient Object serviceObject;
  /**
   * 调用的方法
   */
  private transient Method serviceMethod;
  /**
   * 服务端口号，用于对外发布服务
   */
  private String serverPort;
  /**
   * 服务超时时间
   */
  private long timeout;
  /**
   * 服务提供者唯一标识：唯一标识服务所在的应用，作为Zookeeper服务注册路径中的子路径
   */
  private String appKey;
  /**
   * 服务分组组名：用于分组灰度发布，通过配置不同的分组组名，可以是调用只路由到与调用端相同组名的provider机器组上
   */
  private String groupName = "default";
  /**
   * 服务提供者权重，默认为1，范围为[1-100]，用于实现软负载均衡权重算法
   */
  private int weight = 1;
  /**
   * 服务端线程数：限制该服务能够运行的线程数量，用于实现资源的隔离与服务端限流
   */
  private int workerThreads = 10;

  @Override
  public ProviderInfo clone() throws CloneNotSupportedException {
    super.clone();
    ProviderInfo providerInfo = new ProviderInfo();
    providerInfo.setServiceInterface(serviceInterface);
    providerInfo.setServiceObject(serviceObject);
    providerInfo.setServiceMethod(serviceMethod);
    providerInfo.setServerPort(serverPort);
    providerInfo.setTimeout(timeout);
    providerInfo.setAppKey(appKey);
    providerInfo.setGroupName(groupName);
    providerInfo.setWeight(weight);
    providerInfo.setWorkerThreads(workerThreads);

    return providerInfo;
  }
}
