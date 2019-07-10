package com.liewmanchoi.pigeon.rpc.registry.zookeeper.utils;

import com.liewmanchoi.pigeon.rpc.protocol.InvokerInfo;
import com.liewmanchoi.pigeon.rpc.protocol.ProviderInfo;
import com.liewmanchoi.pigeon.rpc.utils.IpAddressUtils;

/**
 * @author wangsheng
 * @date 2019/6/24
 */
public class NodeUtils {

  private static final String PROVIDER_TYPE = "providers";
  private static final String INVOKER_TYPE = "invokers";

  public static String getCommonPrefix(ProviderInfo providerInfo) {
    return providerInfo.getAppKey()
        + "/"
        + providerInfo.getGroupName()
        + "/"
        + providerInfo.getServiceInterface().getName();
  }

  public static String getCommonPrefix(InvokerInfo invokerInfo) {
    return invokerInfo.getRemoteAppKey()
        + "/"
        + invokerInfo.getGroupName()
        + "/"
        + invokerInfo.getServiceInterface().getName();
  }

  public static String getProviderParentNode(ProviderInfo providerInfo) {
    return providerInfo.getAppKey()
        + "/"
        + providerInfo.getGroupName()
        + "/"
        + providerInfo.getServiceInterface().getName()
        + "/"
        + PROVIDER_TYPE;
  }

  public static String getProviderNode(ProviderInfo providerInfo) {
    return getProviderParentNode(providerInfo)
        + "/"
        + IpAddressUtils.getIpAddress()
        + ":"
        + providerInfo.getServerPort();
  }

  public static String getInvokerParentNode(InvokerInfo invokerInfo) {
    return invokerInfo.getRemoteAppKey()
        + "/"
        + invokerInfo.getGroupName()
        + "/"
        + invokerInfo.getServiceInterface().getName()
        + "/"
        + INVOKER_TYPE;
  }

  public static String getInvokerNode(InvokerInfo invokerInfo) {
    return getInvokerParentNode(invokerInfo) + "/" + IpAddressUtils.getIpAddress();
  }
}
