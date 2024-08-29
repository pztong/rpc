package com.pzt.rpccore.fault.retry;

import com.github.rholder.retry.*;
import com.google.common.base.Predicate;
import com.pzt.rpccore.constant.RpcConstant;
import com.pzt.rpccore.model.RpcResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 随机等待时间
 */
public class RandomWaitTimeRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse call(Callable<RpcResponse> task) throws Exception {
        WaitStrategy waitStrategy = WaitStrategies.randomWait(RpcConstant.MAX_RANDOM_WAIT_TIME, TimeUnit.SECONDS);
        Retryer<RpcResponse> retryer = RetryStrategyUtil.getInstanceByWaitStopStrategy(waitStrategy);
        return retryer.call(task);
    }
}
