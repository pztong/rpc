package com.pzt.rpccore.fault.tolerant;

import com.pzt.rpccore.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("异常：{} 触发容错机制，采用静默处理。", e.getMessage());
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setMessage(e.getMessage());
        return rpcResponse;
    }
}
