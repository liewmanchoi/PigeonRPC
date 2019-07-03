package com.liewmanchoi.pigeon.rpc.registry.api;

import com.liewmanchoi.pigeon.rpc.common.enumeration.ErrorEnum;
import com.liewmanchoi.pigeon.rpc.common.exception.RPCException;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @author wangsheng
 * @date 2019/6/26
 */
@Slf4j
@ToString
public final class ServiceURL {
    @Getter
    private String address;
    // TODO: 值为什么不是String而是List<String>？

    private Map<Key, List<String>> args = new HashMap<>();

    public static final ServiceURL DEFAULT_SERVICE_URL;

    static {
        try {
            DEFAULT_SERVICE_URL = new ServiceURL(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RPCException(ErrorEnum.UNKNOWN_HOST_EXCEPTION, "无法获取到host地址", e);
        }
    }

    private ServiceURL() {}
    private ServiceURL(String address) {
        this.address = address;
    }

    public boolean containsKey(Key key) {
        return args.containsKey(key);
    }

    public List<String> get(Key key) {
        return containsKey(key) ? args.get(key) : Collections.emptyList();
    }

    public static ServiceURL parse(String data) {
        ServiceURL serviceURL = new ServiceURL();
        String[] urlSlices = data.split("\\?");
        serviceURL.address = urlSlices[0];
        //解析URL参数
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

    public enum Key {
        /**
         * ["100"]
         */
        WEIGHT(Collections.singletonList("100"));
        private List<String> defaultValues;

        Key() {}
        Key(List<String> defaultValues) {
            this.defaultValues = defaultValues;
        }

        public List<String> getDefaultValues() {
            return defaultValues != null ? defaultValues : Collections.emptyList();
        }
    }
}
