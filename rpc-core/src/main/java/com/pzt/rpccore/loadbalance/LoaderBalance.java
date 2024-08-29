package com.pzt.rpccore.loadbalance;

import com.pzt.rpccore.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器接口
 */
public interface LoaderBalance {

    /**
     * 从几个服务提供者中选择一个
     * @param list
     * @return
     */
    ServiceMetaInfo select(Map<String,Object> requestParams, List<ServiceMetaInfo> list);
}
