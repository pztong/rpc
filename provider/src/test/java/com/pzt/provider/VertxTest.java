package com.pzt.provider;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTest extends AbstractVerticle {
    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!");
        });

        server.listen(8888, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port 8888");
            } else {
                System.out.println("Failed to bind!");
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new VertxTest());
        try {
            Thread.sleep(10000);
        }catch (Exception e){
            log.info(e.getMessage());
        }
        System.out.println("--------------close__________");
        vertx.close();
    }
}
