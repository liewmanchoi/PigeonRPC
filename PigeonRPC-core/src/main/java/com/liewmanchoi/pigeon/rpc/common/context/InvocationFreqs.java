package com.liewmanchoi.pigeon.rpc.common.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author wangsheng
 * @date 2019/7/3
 */
public final class InvocationFreqs {

  /**
   * 频率统计
   */
  private static final Map<String, LongAdder> FREQS_MAP = new ConcurrentHashMap<>();

  private static String generateKey(String interfaceName, String methodName, String address) {
    return interfaceName + "." + methodName + "." + address;
  }

  public static int getCount(String interfaceName, String methodName, String address) {
    String key = generateKey(interfaceName, methodName, address);
    return FREQS_MAP.computeIfAbsent(key, k -> new LongAdder()).intValue();
  }

  public static void incCount(String interfaceName, String methodName, String address) {
    String key = generateKey(interfaceName, methodName, address);
    FREQS_MAP.computeIfAbsent(key, k -> new LongAdder()).increment();
  }

  public static void decCount(String interfaceName, String methodName, String address) {
    String key = generateKey(interfaceName, methodName, address);
    FREQS_MAP.get(key).decrement();
  }
}
