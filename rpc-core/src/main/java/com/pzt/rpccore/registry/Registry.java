package com.pzt.rpccore.registry;

import com.pzt.rpccore.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心 接口
 */
public interface Registry {

    /**
     * 服务注册
     * @param serviceMetaInfo 服务元信息
     * @return
     */
    boolean registryService(ServiceMetaInfo serviceMetaInfo);

    /**
     * 移除服务
     * @param serviceMetaInfo
     * @return
     */
    boolean removeService(ServiceMetaInfo serviceMetaInfo);

    /**
     *
     * 获取服务
     * @param serviceMetaInfo
     * @return 服务提供者地址列表
     */
    List<ServiceMetaInfo> findService(ServiceMetaInfo serviceMetaInfo);

    /**
     * 心脏跳动机制 需要定期续约
     */
    void heartBeat();

    /**
     * 监听某个key
     * @param key
     */
    void watch(String key);

    /**
     * 关闭注册中心资源
     */
    void destroy();
}
