package com.pzt.rpccore.fault.tolerant;

import com.pzt.rpccore.RpcApplication;
import com.pzt.rpccore.constant.RpcConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 容错策略工厂
 */
@Slf4j
public class TolerantStrategyFactory {

    private static volatile Map<String,TolerantStrategy> map;

    private static void init(){
        map = new HashMap<>();
        ServiceLoader<TolerantStrategy> load = ServiceLoader.load(TolerantStrategy.class);
        for(TolerantStrategy tolerantStrategy:load){
            map.put(tolerantStrategy.getClass().getName(),tolerantStrategy);
        }
    }

    public static TolerantStrategy getInstance(){
        if(map == null){
            synchronized (TolerantStrategy.class){
                if(map == null){
                    init();
                }
            }
        }
        return getInstance(RpcApplication.getRpcConfig().getTolerantStrategy());
    }

    private static TolerantStrategy getInstance(String tolerantName){
        if(!map.containsKey(tolerantName)){
            log.info("The tolerant strategy of {} not found ,return the default strategy.",tolerantName);
            return map.get(RpcConstant.DEFAULT_TOLERANT_STRATEGY);
        }
        return map.get(tolerantName);
    }

}
