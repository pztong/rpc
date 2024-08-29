package com.pzt.rpccore.loadbalance;

import com.google.common.hash.HashCode;
import com.pzt.rpccore.model.ServiceMetaInfo;
import com.google.common.hash.Hashing;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性hash 负载均衡器
 */
public class HashCircleLoaderBalance implements LoaderBalance {
    private final static int VIRTUAL_NODE_COUNT = 50;

    @Override
    public ServiceMetaInfo select(Map<String,Object> requestParams, List<ServiceMetaInfo> list) {
        if (list == null || list.isEmpty())
            return null;
        if (list.size() == 1)
            return list.get(0);
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        int index = -1;
        for (ServiceMetaInfo serviceMetaInfo : list) {
            index++;
            String address = serviceMetaInfo.getHost() + ":" + serviceMetaInfo.getPort();
            for (int i = 0; i < VIRTUAL_NODE_COUNT; i++) {
                String key = address + "#" + i;
                int hash = getHash(key);
                treeMap.put(hash, index);
            }
        }
        //根据请求参数映射到对应的服务器
        int hash = getHash(requestParams.toString());
        Map.Entry<Integer, Integer> entry = treeMap.ceilingEntry(hash);
        if(entry == null)
            return list.get(treeMap.firstEntry().getValue());
        return list.get(entry.getValue());
    }

    private int getHash(String key) {
        return Hashing.murmur3_32_fixed().hashString(key, CharsetUtil.UTF_8).asInt();
    }
}
