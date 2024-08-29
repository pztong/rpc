package com.pzt.rpccore;

import com.github.rholder.retry.*;
import com.google.common.base.Predicate;
import com.pzt.rpccore.exception.BusinessException;
import com.pzt.rpccore.fault.retry.RetryStrategy;
import com.pzt.rpccore.fault.retry.RetryStrategyFactory;
import com.pzt.rpccore.fault.tolerant.TolerantStrategy;
import com.pzt.rpccore.fault.tolerant.TolerantStrategyFactory;
import com.pzt.rpccore.loadbalance.LoaderBalance;
import com.pzt.rpccore.loadbalance.LoaderBalanceFactory;
import com.pzt.rpccore.loadbalance.RandomLoaderBalance;
import com.pzt.rpccore.model.RpcRequest;
import com.pzt.rpccore.model.RpcResponse;
import com.pzt.rpccore.model.ServiceMetaInfo;
import com.pzt.rpccore.protocol.Message;
import com.pzt.rpccore.server.Client;
import com.pzt.rpccore.server.ClientFactory;
import com.pzt.rpccore.utils.MyProtocolUtil;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.internal.ReflectiveTypeFinder;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MyTest {

    @Test
    void tolerantTest() {
    }
    @Test
    void expTest() {
        Exception exception = new Exception("666");
        System.out.println(exception);
    }

    @Test
    void guavaRetryTest() throws Exception {
//        try {
            RetryStrategy instance = RetryStrategyFactory.getInstance();
            System.out.println("重试策略" + instance.getClass().getName());
            Callable<RpcResponse> task = new Callable<>() {
                @Override
                public RpcResponse call() throws Exception {
                    LocalTime localTime = LocalTime.now();
                    DateTimeFormatter pattern = DateTimeFormatter.ofPattern("hh:mm:ss");
                    String formatted = localTime.format(pattern);
                    System.out.println("-------被执行了,时间：" + formatted);
                    RpcResponse rpcResponse = new RpcResponse();
                    rpcResponse.setException(new Exception("结果出错"));
                    if(1 == 1)
                        throw new Exception("----exception----");
                    return rpcResponse;
                }
            };

            instance.call(task);
//        }catch (Exception e){
//            log.info("--------error-------");
//        }

    }



    @Test
    void intTest() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        System.out.println(atomicInteger);
        atomicInteger.incrementAndGet();
        System.out.println(atomicInteger);
        atomicInteger.incrementAndGet();
        System.out.println(atomicInteger);
        atomicInteger.incrementAndGet();
        System.out.println(atomicInteger);

    }

    @Test
    void loaderBalanceTest() {
        LoaderBalance loaderBalance = LoaderBalanceFactory.getInstance("com.pzt.rpccore.loadbalance.HashCircleLoaderBalance");
        System.out.println("loadBalance name:" + loaderBalance.getClass().getName());

        ServiceMetaInfo serviceMetaInfo0 = ServiceMetaInfo.builder()
                .serviceName("user")
                .version("1.0.0")
                .host("localhost")
                .port(8000)
                .build();
        ServiceMetaInfo serviceMetaInfo1 = ServiceMetaInfo.builder()
                .serviceName("user")
                .version("1.0.0")
                .host("localhost")
                .port(8111)
                .build();
        ServiceMetaInfo serviceMetaInfo2 = ServiceMetaInfo.builder()
                .serviceName("user")
                .version("1.0.0")
                .host("localhost")
                .port(8222)
                .build();
        ServiceMetaInfo serviceMetaInfo3 = ServiceMetaInfo.builder()
                .serviceName("user")
                .version("1.0.0")
                .host("localhost")
                .port(8333)
                .build();

        List<ServiceMetaInfo> listUser = new ArrayList<>();

        listUser.add(serviceMetaInfo0);
        listUser.add(serviceMetaInfo1);
        listUser.add(serviceMetaInfo2);
        listUser.add(serviceMetaInfo3);

        ArrayList<ServiceMetaInfo> listTeam = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                    .serviceName("team")
                    .version("1.0.0")
                    .host("localhost")
                    .port(9000 + i)
                    .build();
            listTeam.add(serviceMetaInfo);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("servi67867ceName", "userSer67867vice");
        map.put("6786", "1.0.678670");
        map.put("MethodName", "getUser22257867");
        for (int i = 0; i < 100; i++) {
            ServiceMetaInfo serviceMetaInfo = loaderBalance.select(map, listUser);
            System.out.println("---选中：" + serviceMetaInfo.getServiceName() + serviceMetaInfo.getPort());
            ServiceMetaInfo serviceMetaInfoTeam = loaderBalance.select(map, listTeam);
            System.out.println("---选中：" + serviceMetaInfoTeam.getServiceName() + serviceMetaInfoTeam.getPort());
        }

    }
}
