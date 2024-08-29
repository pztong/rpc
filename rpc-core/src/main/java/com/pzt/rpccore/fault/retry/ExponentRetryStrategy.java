package com.pzt.rpccore.fault.retry;

import com.github.rholder.retry.*;
import com.google.common.base.Predicate;
import com.pzt.rpccore.constant.RpcConstant;
import com.pzt.rpccore.model.RpcResponse;
import com.pzt.rpccore.registry.Registry;
import com.pzt.rpccore.registry.RegistryFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 指数退避算法
 */
public class ExponentRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse call(Callable<RpcResponse> task) throws Exception {
        WaitStrategy waitStrategy = WaitStrategies.exponentialWait(RpcConstant.EXPONENT_MULTIPLIER, RpcConstant.EXPONENT_MAX_WAIT_TIME, TimeUnit.SECONDS);
        Retryer<RpcResponse> retryer = RetryStrategyUtil.getInstanceByWaitStopStrategy(waitStrategy);
        return retryer.call(task);
    }
}
