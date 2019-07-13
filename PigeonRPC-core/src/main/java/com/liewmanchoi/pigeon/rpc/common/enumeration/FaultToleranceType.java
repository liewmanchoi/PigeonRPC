package com.liewmanchoi.pigeon.rpc.common.enumeration;

import com.liewmanchoi.pigeon.rpc.cluster.api.FaultToleranceHandler;
import com.liewmanchoi.pigeon.rpc.cluster.faulttolerance.FailFastHandler;
import com.liewmanchoi.pigeon.rpc.cluster.faulttolerance.FailOverHandler;
import com.liewmanchoi.pigeon.rpc.cluster.faulttolerance.FailSafeHandler;
import com.liewmanchoi.pigeon.rpc.common.enumeration.support.ExtensionBaseType;

/**
 * @author wangsheng
 * @date 2019/7/13
 */
public enum FaultToleranceType implements ExtensionBaseType<FaultToleranceHandler> {
  /**
   * FailFast
   */
  FAIL_FAST(new FailFastHandler()),
  /**
   * FailOver
   */
  FAIL_OVER(new FailOverHandler()),
  /**
   * FailSafe
   */
  FAIL_SAFE(new FailSafeHandler());

  private FaultToleranceHandler faultToleranceHandler;

  FaultToleranceType(FaultToleranceHandler faultToleranceHandler) {
    this.faultToleranceHandler = faultToleranceHandler;
  }

  @Override
  public FaultToleranceHandler getInstance() {
    return faultToleranceHandler;
  }
}
