package com.pzt.rpccore.fault.retry;

import com.github.rholder.retry.*;
import com.google.common.base.Predicate;
import com.pzt.rpccore.constant.RpcConstant;
import com.pzt.rpccore.model.RpcResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定等待时间的重试策略
 */
public class FixWaitTimeRetryStrategy implements RetryStrategy {

    @Override
    public RpcResponse call(Callable<RpcResponse> task) throws Exception {
        WaitStrategy waitStrategy = WaitStrategies.fixedWait(RpcConstant.FIX_WAIT_TIME, TimeUnit.SECONDS);
        Retryer<RpcResponse> retryer = RetryStrategyUtil.getInstanceByWaitStopStrategy(waitStrategy);
        return retryer.call(task);
    }
}
