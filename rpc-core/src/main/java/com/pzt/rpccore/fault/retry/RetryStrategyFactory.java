package com.pzt.rpccore.fault.retry;

import com.pzt.rpccore.RpcApplication;
import com.pzt.rpccore.constant.RpcConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 重试策略工厂
 */
@Slf4j
public class RetryStrategyFactory {

    private static volatile Map<String, RetryStrategy> map;

    private static void init() {
        map = new HashMap<>();
        ServiceLoader<RetryStrategy> loader = ServiceLoader.load(RetryStrategy.class);
        for(RetryStrategy retryStrategy:loader){
            map.put(retryStrategy.getClass().getName(),retryStrategy);
        }
    }

    public static RetryStrategy getInstance() {
        if (map == null) {
            synchronized (RetryStrategyFactory.class) {
                if (map == null) {
                    init();
                }
            }
        }
        String retryStrategyName = RpcApplication.getRpcConfig().getRetryStrategy();
        return getInstance(retryStrategyName);
    }

    private static RetryStrategy getInstance(String retryStrategyName) {
        if(!map.containsKey(retryStrategyName)){
            log.info("The retry strategy of {} not found, return default {}",retryStrategyName, RpcConstant.DEFAULT_RETRY_STRATEGY);
            return map.get(RpcConstant.DEFAULT_RETRY_STRATEGY);
        }
        return map.get(retryStrategyName);
    }

}
