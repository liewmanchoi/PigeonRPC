package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.serialization.api.Serializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局配置类。类中持有相关配置的实例
 *
 * @author wangsheng
 * @date 2019/6/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalConfig {
    // TODO: GlobalConfig待完成

    public Serializer getSerializer() {
        // TODO: getSerializer()
        return null;
    }
}
