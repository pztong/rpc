package com.pzt.rpccore.config;

import lombok.Data;

/**
 * 注册中心配置类
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心 默认值："com.pzt.rpccore.registry.EtcdRegistry"
     */
    private String registry = "com.pzt.rpccore.registry.EtcdRegistry";
    /**
     * 注册中心地址 默认值："http://localhost:2379"
     */
    private String address = "http://localhost:2379";
    /**
     * 注册中心用户名 默认值："root"
     */
    private String userName = "root";
    /**
     * 注册中心密码 默认值："123456"
     */
    private String password = "123456";
    /**
     * 超时时间 默认值：10000ms
     */
    private Long timeout = 10000L;

}
