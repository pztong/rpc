package com.pzt.rpccore.fault.tolerant;

import com.pzt.rpccore.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 */
public interface TolerantStrategy {

    /**
     *
     * 执行容错处理
     * @param context  上下文
     * @param e 异常信息
     * @return
     */
    RpcResponse doTolerant(Map<String,Object> context,Exception e);
}
