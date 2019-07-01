package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.protocol.api.protocol.Protocol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/7/1
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolConfig {
    public static final int DEFAULT_PORT = 8000;
    private String type;
    private Integer port;

    private Protocol protocolInstance;

    private int getPort() {
        if (port != null) {
            return port;
        }

        return DEFAULT_PORT;
    }

    // TODO: ProtocolConfig#close()方法
}
