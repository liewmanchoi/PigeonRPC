package com.liewmanchoi.pigeon.rpc.registry.api;

import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/6/26
 */
@Slf4j
@EqualsAndHashCode(of = {"address"})
@ToString
public final class ServiceURL {

  public static final ServiceURL DEFAULT_SERVICE_URL;
  // TODO: 值为什么不是String而是List<String>？

  static {
    try {
      DEFAULT_SERVICE_URL = new ServiceURL(InetAddress.getLocalHost().getHostAddress());
    } catch (UnknownHostException e) {
      throw new RPCException(ErrorEnum.UNKNOWN_HOST_EXCEPTION, "无法获取到host地址", e);
    }
  }

  @Getter
  private String address;
  private Map<Key, List<String>> args = new HashMap<>();

  private ServiceURL() {
  }

  private ServiceURL(String address) {
    this.address = address;
  }

  public static ServiceURL parse(String data) {
    ServiceURL serviceURL = new ServiceURL();
    String[] urlSlices = data.split("\\?");
    serviceURL.address = urlSlices[0];
    // 解析URL参数
    if (urlSlices.length > 1) {
      String params = urlSlices[1];
      String[] urlParams = params.split("&");
      for (String param : urlParams) {
        String[] kv = param.split("=");
        String key = kv[0];
        try {
          Key keyEnum = Key.valueOf(key.toUpperCase());

          String[] values = kv[1].split(",");
          serviceURL.args.put(keyEnum, Arrays.asList(values));
        } catch (IllegalArgumentException e) {
          log.error("key {} 不存在 ", key);
        }
      }
    }
    return serviceURL;
  }

  public boolean containsKey(Key key) {
    return args.containsKey(key);
  }

  public List<String> get(Key key) {
    return containsKey(key) ? args.get(key) : Collections.emptyList();
  }

  public enum Key {
    /**
     * ["100"]
     */
    WEIGHT(Collections.singletonList("100"));
    private List<String> defaultValues;

    Key() {
    }

    Key(List<String> defaultValues) {
      this.defaultValues = defaultValues;
    }

    public List<String> getDefaultValues() {
      return defaultValues != null ? defaultValues : Collections.emptyList();
    }
  }
}
