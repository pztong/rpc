package com.pzt.rpccore.fault.retry;

import com.pzt.rpccore.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略接口
 */
public interface RetryStrategy {

    /**
     * 为这个任务添加重试机制
     * @param task 任务
     * @return
     */
    RpcResponse call(Callable<RpcResponse> task) throws Exception;
}
