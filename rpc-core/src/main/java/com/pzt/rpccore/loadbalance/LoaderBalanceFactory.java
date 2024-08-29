package com.pzt.rpccore.loadbalance;

import com.pzt.rpccore.RpcApplication;
import com.pzt.rpccore.constant.RpcConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负载均衡器工厂
 */
@Slf4j
public class LoaderBalanceFactory {
    private final static Map<String, LoaderBalance> loaderMap;

    static {
        HashMap<String, LoaderBalance> map = new HashMap<>();
        ServiceLoader<LoaderBalance> load = ServiceLoader.load(LoaderBalance.class);
        for (LoaderBalance loader : load) {
            map.put(loader.getClass().getName(), loader);
        }
        loaderMap = map;
    }

    /**
     * 获取一个负载均衡器
     *
     * @return
     */
    public static LoaderBalance getInstance() {
        String loaderName = RpcApplication.getRpcConfig().getLoaderBalance();
        return getInstance(loaderName);
    }

    /**
     * 根据负载均衡器全类名获取
     *
     * @param loaderName
     * @return
     */
    public static LoaderBalance getInstance(String loaderName) {
        if (!loaderMap.containsKey(loaderName)) {
            log.info("The loader balance of {} not found, return default loader balance {}", loaderName, RpcConstant.DEFAULT_LOADER_BALANCE);
            return loaderMap.get(RpcConstant.DEFAULT_LOADER_BALANCE);
        }
        return loaderMap.get(loaderName);
    }
}
