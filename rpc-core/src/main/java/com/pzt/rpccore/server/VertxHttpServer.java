package com.pzt.rpccore.server;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * Vert.x web
 */
@Slf4j
public class VertxHttpServer implements Server {
    private final Vertx vertx = Vertx.vertx();
    private final io.vertx.core.http.HttpServer server = vertx.createHttpServer();
    @Override
    public void doStart(int port) {
        //绑定请求处理器
        server.requestHandler(new VertxHttpServerHandler());
        //绑定端口 开始监听
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("Server is now listening on port:" + port);
            } else {
                log.info("Failed to start server:" + result.cause());
            }
        });
    }

    @Override
    public void doStop() {
        vertx.close();
    }
}
