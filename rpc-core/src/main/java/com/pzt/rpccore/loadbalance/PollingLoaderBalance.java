package com.pzt.rpccore.loadbalance;

import com.pzt.rpccore.model.ServiceMetaInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡器
 */
public class PollingLoaderBalance implements LoaderBalance {
    private final Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

    @Override
    public ServiceMetaInfo select(Map<String,Object> requestParams, List<ServiceMetaInfo> list) {
        if (list == null || list.isEmpty())
            return null;
        if (list.size() == 1)
            return list.get(0);
        //所有的服务都是用同一个id自增，感觉不太好
        //每个服务都有自己的 id
        ServiceMetaInfo serviceMetaInfo = list.get(0);
        String key = serviceMetaInfo.getServiceName() + ":" + serviceMetaInfo.getVersion();
        if (!map.containsKey(key)) {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            map.put(key, atomicInteger);
            return serviceMetaInfo;
        }
        AtomicInteger atomicInteger = map.get(key);
        return list.get(atomicInteger.incrementAndGet() % list.size());
    }
}
