package com.liewmanchoi.pigeon.rpc.config;

import com.liewmanchoi.pigeon.rpc.cluster.api.FaultToleranceHandler;
import com.liewmanchoi.pigeon.rpc.cluster.api.LoadBalancer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangsheng
 * @date 2019/7/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterConfig {
  private String loadBalancer;
  private String faultTolerance;
  private LoadBalancer loadBalancerInstance;
  private FaultToleranceHandler faultToleranceHandlerInstance;
}
