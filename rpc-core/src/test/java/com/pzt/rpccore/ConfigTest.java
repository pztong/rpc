package com.pzt.rpccore;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.esotericsoftware.minlog.Log;
import com.pzt.rpccore.config.RpcConfig;
import com.pzt.rpccore.constant.RpcConstant;
import com.pzt.rpccore.model.RpcRequest;
import com.pzt.rpccore.model.ServiceMetaInfo;
import com.pzt.rpccore.registry.LocalRegistry;
import com.pzt.rpccore.registry.Registry;
import com.pzt.rpccore.registry.RegistryFactory;
import com.pzt.rpccore.serializer.Serializer;
import com.pzt.rpccore.serializer.SerializerFactory;
import com.pzt.rpccore.server.VertxHttpServer;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.support.CloseableClient;
import io.etcd.jetcd.watch.WatchEvent;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
public class ConfigTest {



    @Test
    void objectTest(){
        RpcRequest rpcRequest = new RpcRequest();
        set(rpcRequest);
        System.out.println(rpcRequest);
    }

    void set(RpcRequest rpcRequest){
        rpcRequest.setServiceName("pzt 666");
    }

    @Test
    void jvmHookTest() {
        System.out.println("hello world, I am provider.");
        Registry registry = RegistryFactory.getRegistry();

        ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                .serviceName("userService")
                .version("1.0.0")
                .host(RpcApplication.getRpcConfig().getServerHost())
                .port(RpcApplication.getRpcConfig().getServerPort())
                .build();
        ServiceMetaInfo serviceMetaInfo2 = ServiceMetaInfo.builder()
                .serviceName("userService666")
                .version("2.0.0")
                .host(RpcApplication.getRpcConfig().getServerHost())
                .port(RpcApplication.getRpcConfig().getServerPort())
                .build();

        ServiceMetaInfo serviceMetaInfo666 = ServiceMetaInfo.builder()
                .serviceName("666")
                .version("6.6.6")
                .build();
        //注册服务
//        LocalRegistry.register(serviceMetaInfo.getServiceName(), serviceMetaInfo.getVersion(), String.class);
//        LocalRegistry.register(serviceMetaInfo2.getServiceName(), serviceMetaInfo2.getVersion(), String.class);
        //在注册中心注册
        registry.registryService(serviceMetaInfo);
        registry.registryService(serviceMetaInfo2);
//        List<ServiceMetaInfo> list = registry.findService(serviceMetaInfo666);
//        VertxHttpServer vertxHttpServer = new VertxHttpServer();
//        vertxHttpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("------close ------");
//        vertxHttpServer.doStop();
    }

    @Test
    void watchTest() {
        Registry registry = RegistryFactory.getRegistry();
        ServiceMetaInfo serviceMetaInfo1 = ServiceMetaInfo.builder()
                .serviceName("userService")
                .version("6.6.6")
                .host("localhost")
                .port(9001)
                .build();
        ServiceMetaInfo serviceMetaInfo2 = ServiceMetaInfo.builder()
                .serviceName("userService")
                .version("6.6.6")
                .host("localhost")
                .port(9002)
                .build();

        ServiceMetaInfo userService = ServiceMetaInfo.builder()
                .serviceName("userService")
                .version("6.6.6")
                .build();

        registry.registryService(serviceMetaInfo1);
        registry.registryService(serviceMetaInfo2);

        List<ServiceMetaInfo> list = registry.findService(userService);
//
//        ServiceMetaInfo serviceMetaInfo3 = ServiceMetaInfo.builder()
//                .serviceName("userService")
//                .version("6.6.6")
//                .host("localhost")
//                .port(9003)
//                .build();
//        ServiceMetaInfo serviceMetaInfo4 = ServiceMetaInfo.builder()
//                .serviceName("userService")
//                .version("6.6.6")
//                .host("localhost")
//                .port(9004)
//                .build();
//        System.out.println("------------");
//        registry.registryService(serviceMetaInfo3);
//        System.out.println("------------");
//        registry.registryService(serviceMetaInfo4);
//        System.out.println("------------");
//        registry.removeService(serviceMetaInfo1);
//        System.out.println("------------");

        try {
            Thread.sleep(15000);
        } catch (Exception e) {
            log.info(e.getMessage());
        }


    }

    @Test
    void keepAliveTest() {
        try {
            Client client = Client.builder().endpoints("http://localhost:2379").build();
            KV kvClient = client.getKVClient();
            Lease leaseClient = client.getLeaseClient();
            Watch watchClient = client.getWatchClient();

            // 创建一个租约并获取租约ID
            long leaseId = leaseClient.grant(10).get().getID(); // 5秒TTL

            // 将Key绑定到租约上
            ByteSequence key = ByteSequence.from("/my_key".getBytes());
            ByteSequence value = ByteSequence.from("my_value".getBytes());
            kvClient.put(key, value, PutOption.builder().withLeaseId(leaseId).build()).get();

            // 启动keepAlive
            CloseableClient closeableClient = leaseClient.keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
                @Override
                public void onNext(LeaseKeepAliveResponse response) {
                    log.info("--------------------------------------");
                    System.out.println("Lease " + leaseId + " keep alive, TTL: " + response.getTTL());
                }

                @Override
                public void onError(Throwable throwable) {
                    log.info("error--------------------------------------");
                    log.info(throwable.getMessage());
                }

                @Override
                public void onCompleted() {

                }
            });

            // 启动watch监听器
            watchClient.watch(key, watchResponse -> {
                for (WatchEvent event : watchResponse.getEvents()) {
                    log.info("--------------------------------------");
                    System.out.println("Watch event: " + event.getEventType() + " for key: " + new String(event.getKeyValue().getKey().getBytes()));
                }
            });

            // 为了保持main方法运行，可以使用Thread.sleep来模拟实际的长时间运行
            Thread.sleep(60000); // 保持程序运行1分钟

            // 关闭资源
            closeableClient.close();
            client.close();
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }


    @Test
    void registryFactoryTest() {
        Registry registry1 = RegistryFactory.getRegistry();
        System.out.println(registry1);

        Registry registry2 = RegistryFactory.getRegistry();
        System.out.println(registry2);
    }

    @Test
    void cronTest() {
        String cronString = "*/" + RpcConstant.DEFAULT_LEASE_PERIOD + " * * * * *";
        CronUtil.schedule(cronString, new Task() {
            @Override
            public void execute() {
                log.info("-------heart beat -----------");
                // 获取当前时间
                LocalTime currentTime = LocalTime.now();

                // 定义格式化模式
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                // 格式化时间
                String formattedTime = currentTime.format(formatter);
                // 打印格式化后的时间
                System.out.println("当前时间: " + formattedTime);
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
        try {
            Thread.sleep(1000000);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        System.out.println("over");

    }

    @Test
    void etcdTest() {

        ServiceMetaInfo serviceMetaInfo1 = ServiceMetaInfo.builder()
                .serviceName("userService")
                .version("6.6.6")
                .host("localhost")
                .port(9000)
                .build();
        ServiceMetaInfo serviceMetaInfo2 = ServiceMetaInfo.builder()
                .serviceName("userService")
                .version("6.6.6")
                .host("localhost")
                .port(9001)
                .build();
        ServiceMetaInfo serviceMetaInfo3 = ServiceMetaInfo.builder()
                .serviceName("userService")
                .version("6.6.6")
                .host("localhost")
                .port(9002)
                .build();
        ServiceMetaInfo serviceMetaInfo4 = ServiceMetaInfo.builder()
                .serviceName("teamService")
                .version("1.0.0")
                .host("localhost")
                .port(8000)
                .build();

        Registry registry = RegistryFactory.getRegistry();
        log.info("--------registry:{}------", registry);
        registry.registryService(serviceMetaInfo1);
        registry.registryService(serviceMetaInfo2);
        registry.registryService(serviceMetaInfo3);
        registry.registryService(serviceMetaInfo4);

        ServiceMetaInfo userService = ServiceMetaInfo.builder()
                .serviceName("userService")
                .version("6.6.6")
                .build();


        List<ServiceMetaInfo> list = registry.findService(userService);
        System.out.println("---------list:" + list);

        List<ServiceMetaInfo> list2 = registry.findService(userService);
        System.out.println("---------list2:" + list2);


        registry.removeService(serviceMetaInfo1);

        List<ServiceMetaInfo> list1 = registry.findService(userService);
        System.out.println("---------list1:" + list1);


        try {
            Thread.sleep(30000);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        System.out.println("over");

    }

    @Test
    void tempTest() {
        ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                .serviceName("userService")
                .version("6.6.6")
                .build();
        Client client = Client.builder().endpoints("http://localhost:2379").build();
        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("/hello".getBytes());
        try {
            GetResponse getResponse = kvClient.get(key).get();
            getResponse.getKvs().forEach(kv -> {
                System.out.println("----value:" + kv.getValue().toString());
            });
        } catch (Exception e) {
            log.info("--------error----------{}", e.getMessage());
        }


    }

    @Test
    void propertyTest() {
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        System.out.println("rpcConfig:" + rpcConfig);

    }


    @Test
    void serializerTest() {
        Serializer serializer = SerializerFactory.getSerializer();
        System.out.println("serializer:" + serializer);

        RpcRequest rpcRequest = new RpcRequest().builder()
                .serviceName("userService")
                .methodName("getUser")
                .parameterTypes(new Class[]{String.class, Integer.class})
                .args(new Object[]{"pzt", 1})
                .build();
        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            Object object = serializer.deSerialize(bytes, RpcRequest.class);
            System.out.println(object);
        } catch (Exception e) {
            Log.info(e.getMessage());
        }


    }


}
