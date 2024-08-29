package com.pzt.rpccore.server;

/**
 * web 服务器接口
 */
public interface Server {

    /**
     * 启动服务器
     * @param port 端口
     */
    void doStart(int port);

    void doStop();
}
