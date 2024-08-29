package com.pzt.provider;

import com.pzt.common.UserService;
import com.pzt.provider.service.UserServiceImpl;
import com.pzt.rpccore.RpcApplication;
import com.pzt.rpccore.model.ServiceMetaInfo;
import com.pzt.rpccore.registry.LocalRegistry;
import com.pzt.rpccore.registry.RegistryFactory;
import com.pzt.rpccore.server.MyProtocolServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProviderTest2 {
    public static void main(String[] args) {
        System.out.println("hello world, I am provider.");

        ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                .serviceName(UserService.class.getName())
                .version("1.0.0")
                .host(RpcApplication.getRpcConfig().getServerHost())
                .port(8222)
                .build();
        ServiceMetaInfo serviceMetaInfo2 = ServiceMetaInfo.builder()
                .serviceName(UserService.class.getName())
                .version("2.2.2")
                .host(RpcApplication.getRpcConfig().getServerHost())
                .port(8222)
                .build();
        //注册服务
        LocalRegistry.register(serviceMetaInfo.getServiceName(), serviceMetaInfo.getVersion(), UserServiceImpl.class);
        //在注册中心注册
        RegistryFactory.getRegistry().registryService(serviceMetaInfo);
        RegistryFactory.getRegistry().registryService(serviceMetaInfo2);

        //启动服务器
        MyProtocolServer server = new MyProtocolServer();
        server.doStart(8222);


//        VertxHttpServer vertxHttpServer = new VertxHttpServer();
//        vertxHttpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

//        try {
//            Thread.sleep(15000);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//        System.out.println("------close ------");
//        vertxHttpServer.doStop();
    }
}
