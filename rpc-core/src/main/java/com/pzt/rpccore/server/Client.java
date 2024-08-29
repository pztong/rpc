package com.pzt.rpccore.server;

import com.pzt.rpccore.model.RpcRequest;
import com.pzt.rpccore.model.RpcResponse;
import com.pzt.rpccore.protocol.Message;

import java.util.concurrent.CompletableFuture;

/**
 * 客户端
 */
public interface Client {
    /**
     * 发送请求 得到响应后才能发送下一个请求
     * @param requestMessage  请求报文
     * @return
     */
    RpcResponse send(Message<RpcRequest> requestMessage);

    /**
     * 异步请求 得到响应前可以发送多个请求
     * @param requestMessage
     * @return
     */
    CompletableFuture<RpcResponse> sendAsync(Message<RpcRequest> requestMessage);

    /**
     * 关闭客户端
     */
    void close();

    /**
     * 查看客户端是否还在连接
     * @return
     */
    boolean isAlive();
}
