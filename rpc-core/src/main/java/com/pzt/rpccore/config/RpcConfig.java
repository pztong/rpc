package com.pzt.rpccore.config;

import lombok.Data;

/**
 * rpc配置信息
 */
@Data
public class RpcConfig {
    /**
     * 框架名称
     */
    private String name = "pzt-rpc";
    /**
     * 框架版本
     */
    private String version = "1.0.0";
    /**
     * 服务器地址
     */
    private String serverHost = "localhost";
    /**
     * 服务器端口
     */
    private int serverPort = 8000;

    /**
     * 模拟接口 默认关闭
     */
    private boolean mock = false;

    /**
     * 序列化器全类名
     */
    private String serializer = "com.pzt.rpccore.serializer.JdkSerializer";

    /**
     * 负载均衡器
     * 默认轮询的方式
     */
    private String loaderBalance = "com.pzt.rpccore.loadbalance.PollingLoaderBalance";

    /**
     * 重试策略  默认指数退避
     */
    private String retryStrategy = "com.pzt.rpccore.fault.retry.ExponentRetryStrategy";

    /**
     * 容错策略
     * 默认降级处理
     */
    private String tolerantStrategy = "com.pzt.rpccore.fault.tolerant.FailBackTolerantStrategy";

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();
}
