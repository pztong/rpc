package com.pzt.rpccore.loadbalance;

import com.pzt.rpccore.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 随机选择 负载均衡器
 */
public class RandomLoaderBalance implements LoaderBalance{
    @Override
    public ServiceMetaInfo select(Map<String,Object> requestParams, List<ServiceMetaInfo> list) {
        if (list == null || list.isEmpty())
            return null;
        if (list.size() == 1)
            return list.get(0);
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }
}
