package com.pzt.rpccore.registry;

import com.esotericsoftware.minlog.Log;
import com.pzt.rpccore.RpcApplication;
import com.pzt.rpccore.constant.RpcConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心工厂 使用spi机制
 */
@Slf4j
public class RegistryFactory {

    private static volatile Map<String,Registry> registryMap;

    /**
     * 获取配置文件中的Register
     * @return
     */
    public static Registry getRegistry(){
        String registryName = RpcApplication.getRpcConfig().getRegistryConfig().getRegistry();
        return getRegistry(registryName);
    }

    public static Registry getRegistry(String registryName){
        if(registryMap == null){
            synchronized (RegistryFactory.class){
                if(registryMap == null){
                    init();
                }
            }
        }
        if(!registryMap.containsKey(registryName)){
            log.info("The registry of {} is not existent.Return the default registry:{}.",registryName, RpcConstant.DEFAULT_REGISTRY);
            return registryMap.get(RpcConstant.DEFAULT_REGISTRY);
        }
        return registryMap.get(registryName);
    }

    private static void init(){
        //加载注册中心
        loadAllRegistry();
    }

    private static void loadAllRegistry(){
        ConcurrentHashMap<String, Registry> concurrentHashMap = new ConcurrentHashMap<>();
        ServiceLoader<Registry> loader = ServiceLoader.load(Registry.class);
        for(Registry registry: loader){
            concurrentHashMap.put(registry.getClass().getName(),registry);
        }
        registryMap = concurrentHashMap;
    }
}
