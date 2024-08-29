package com.pzt.rpccore.fault.retry;

import com.pzt.rpccore.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不重试
 */
public class NoRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse call(Callable<RpcResponse> task) throws Exception {
        return task.call();
    }
}
