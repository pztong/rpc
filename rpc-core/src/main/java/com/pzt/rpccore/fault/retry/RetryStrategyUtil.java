package com.pzt.rpccore.fault.retry;

import com.github.rholder.retry.*;
import com.google.common.base.Predicate;
import com.pzt.rpccore.constant.RpcConstant;
import com.pzt.rpccore.model.RpcResponse;

import java.util.concurrent.TimeUnit;

/**
 * 生成重试策略的工具类
 */
public class RetryStrategyUtil {

    public static Retryer<RpcResponse> getInstanceByWaitStopStrategy(WaitStrategy waitStrategy) {
        long startTime = System.nanoTime();
        return RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfException()
                .retryIfResult(new Predicate<RpcResponse>() {
                    @Override
                    public boolean apply(RpcResponse input) {
                        return input.getException() != null;
                    }
                })
                .withWaitStrategy(waitStrategy)
                .withStopStrategy(
                        new StopStrategy() {
                            @Override
                            public boolean shouldStop(Attempt attempt) {
                                boolean stopAfterAttempts = attempt.getAttemptNumber() >= RpcConstant.MAX_RETRY_COUNT;
                                boolean stopAfterDuration = (System.nanoTime() - startTime) > TimeUnit.SECONDS.toNanos(RpcConstant.RETRY_MAX_DURATION_TIME);
                                return stopAfterAttempts || stopAfterDuration;
                            }
                        }
                )
                .build();
    }

}
