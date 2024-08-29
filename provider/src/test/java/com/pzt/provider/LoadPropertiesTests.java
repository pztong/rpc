package com.pzt.provider;

import com.pzt.common.UserService;
import com.pzt.provider.service.UserServiceImpl;
import com.pzt.rpccore.RpcApplication;
import com.pzt.rpccore.config.RpcConfig;
import com.pzt.rpccore.model.ServiceMetaInfo;
import com.pzt.rpccore.registry.LocalRegistry;
import com.pzt.rpccore.registry.RegistryFactory;
import com.pzt.rpccore.server.VertxHttpServer;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;


@Slf4j
public class LoadPropertiesTests {

    @Test
    void PropertiesTest() {
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        System.out.println(rpcConfig);
    }

    @Test
    void jvmHookTest(){
        System.out.println("hello world, I am provider.");

        ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                .serviceName(UserService.class.getName())
                .version("1.0.0")
                .host(RpcApplication.getRpcConfig().getServerHost())
                .port(RpcApplication.getRpcConfig().getServerPort())
                .build();
        ServiceMetaInfo serviceMetaInfo2 = ServiceMetaInfo.builder()
                .serviceName(UserService.class.getName())
                .version("2.0.0")
                .host(RpcApplication.getRpcConfig().getServerHost())
                .port(RpcApplication.getRpcConfig().getServerPort())
                .build();
        //注册服务
        LocalRegistry.register(serviceMetaInfo.getServiceName(), serviceMetaInfo.getVersion(), UserServiceImpl.class);
        LocalRegistry.register(serviceMetaInfo2.getServiceName(), serviceMetaInfo2.getVersion(), UserServiceImpl.class);
        //在注册中心注册
        RegistryFactory.getRegistry().registryService(serviceMetaInfo);
        RegistryFactory.getRegistry().registryService(serviceMetaInfo2);

        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

        try {
            Thread.sleep(15000);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("------close ------");
        vertxHttpServer.doStop();
    }

    @Test
    void vertxTest() {
        io.vertx.core.http.HttpServer server = Vertx.vertx().createHttpServer();
        server.requestHandler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!");
        });
        //绑定端口 开始监听
        int port = 8000;
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("Server is now listening on port:" + port);
            } else {
                log.info("Failed to start server:" + result.cause());
            }
        });
//        System.out.println("-------close--------");
//        server.close();
    }
}
