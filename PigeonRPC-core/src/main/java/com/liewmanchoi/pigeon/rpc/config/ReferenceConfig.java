package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequest;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCRequestWrapper;
import com.liewmanchoi.pigeon.rpc.common.domain.RPCResponse;
import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.enumeration.InvokeMode;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import com.liewmanchoi.pigeon.rpc.common.utils.GlobalRecycler;
import com.liewmanchoi.pigeon.rpc.filter.Filter;
import com.liewmanchoi.pigeon.rpc.protocol.api.invoker.Invoker;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务引用配置
 *
 * @author wangsheng
 * @date 2019/6/30
 */
@Data
@Builder
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ReferenceConfig<T> extends AbstractConfig {
  /** interfaceName -> referenceConfig */
  private static final Map<String, ReferenceConfig<?>> REFERENCE_CONFIG_MAP =
      new ConcurrentHashMap<>();

  private String interfaceName;
  private Class<T> interfaceClass;
  private long timeout;
  private InvokeMode invokeMode;

  private List<Filter> filters;
  /** 是否为泛化调用 */
  private boolean isGeneric;

  private volatile T reference;
  private boolean initialized;

  @EqualsAndHashCode.Exclude @ToString.Exclude private volatile Invoker<T> invoker;

  @SuppressWarnings("unchecked")
  public static <T> ReferenceConfig<T> createReferenceConfig(
      String interfaceName,
      Class<T> interfaceClass,
      InvokeMode invokeMode,
      long timeout,
      boolean isGeneric,
      List<Filter> filters) {
    if (REFERENCE_CONFIG_MAP.containsKey(interfaceName)) {
      return (ReferenceConfig<T>) REFERENCE_CONFIG_MAP.get(interfaceName);
    }

    ReferenceConfig<T> config =
        (ReferenceConfig<T>)
            ReferenceConfig.builder()
                .interfaceName(interfaceName)
                .interfaceClass((Class<Object>) interfaceClass)
                .timeout(timeout)
                .invokeMode(invokeMode)
                .isGeneric(isGeneric)
                .filters(filters != null ? filters : new ArrayList<>())
                .build();
    REFERENCE_CONFIG_MAP.put(interfaceName, config);
    return config;
  }

  public static ReferenceConfig<?> getConfigByInterfaceName(String interfaceName) {
    return REFERENCE_CONFIG_MAP.get(interfaceName);
  }

  private void init() {
    if (initialized) {
      return;
    }

    initialized = true;

    // 根据本配置类的配置，引用ClusterInvoker
    invoker = getClusterConfig().getLoadBalancerInstance().referCluster(this);

    if (!isGeneric) {
      // 如果不是泛化调用，则创建代理类对象
      reference = getApplicationConfig().getProxyFactoryInstance().createProxy(invoker);
    }
  }

  /**
   * 返回持有的代理类对象
   *
   * @return 代理类对象
   */
  public T get() {
    if (!initialized) {
      init();
    }

    return reference;
  }

  public String getInterfaceName() {
    return interfaceClass.getName();
  }

  /**
   * 发起泛化调用
   *
   * @param methodName 方法名
   * @param argTypes 参数类型
   * @param args 方法参数
   * @return 调用结果
   */
  public Object invokeForGeneric(String methodName, Class<?>[] argTypes, Object[] args) {
    if (!initialized) {
      // 保证已经引用了服务
      init();
    }

    if (isGeneric) {
      RPCRequest request = GlobalRecycler.reuse(RPCRequest.class);
      String interfaceName = interfaceClass.getName();
      log.info("发起泛化调用，接口名[{}]，方法名[{}]", interfaceName, methodName);

      // 设置request内容
      request.setRequestId(UUID.randomUUID().toString());
      request.setInterfaceName(interfaceName);
      request.setMethodName(methodName);
      request.setArgTypes(argTypes);
      request.setArgs(args);

      RPCRequestWrapper requestWrapper =
          RPCRequestWrapper.builder().rpcRequest(request).referenceConfig(this).build();
      RPCResponse response = invoker.invoke(requestWrapper);
      if (response == null) {
        return null;
      }

      return response.getResult();
    }
    // 如果不是泛化调用，则抛出异常
    throw new RPCException(ErrorEnum.GENERIC_INVOCATION_ERROR, "非泛化调用不能调用本方法");
  }
}
