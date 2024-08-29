package com.pzt.rpccore.constant;

import com.pzt.rpccore.serializer.JsonSerializer;

/**
 * RPC框架中的一些常量
 */
public interface RpcConstant {
    /**
     * 这个框架的配置的前缀
     */
    String DEFAULT_CONFIG_PREFIX = "pzt.rpc";

    /**
     * 默认序列号器 com.pzt.rpccore.serializer.JsonSerializer
     */
    String DEFAULT_SERIALIZER = "com.pzt.rpccore.serializer.JsonSerializer";

    /**
     * 默认负载均衡器 轮询
     */
    String DEFAULT_LOADER_BALANCE = "com.pzt.rpccore.loadbalance.PollingLoaderBalance";
    /**
     * 默认注册中心实现类
     */
    String DEFAULT_REGISTRY = "com.pzt.rpccore.registry.EtcdRegistry";

    /**
     * 在注册中心注册服务时的默认前缀
     */
    String REGISTRY_DEFAULT_PREFIX = "/pzt_rpc/";

    /**
     * 服务默认分组
     */
    String DEFAULT_SERVICE_GROUP = "default";


    /**
     * 服务默认版本
     */
    String DEFAULT_SERVICE_VERSION = "1.0.0";
    /**
     * 服务的TTL 单位 秒 默认30S
     */
    Long DEFAULT_KEY_TTL = 30L;


    /**
     * 服务续约周期 默认 10S 续约一次
     * 使用keepAlive实现自动续约  这个没用了
     */
    @Deprecated
    Long DEFAULT_LEASE_PERIOD = 10L;

    /**
     * http请求前缀
     */
    String HTTP_PREFIX = "http://";

    /**
     * 保持连接的客户端的最大数量
     */
    int CLIENT_KEEP_ALIVE_MAX_NUMBER = 4;

    /**
     * 重试策略 固定等待时间 3秒
     */
    long FIX_WAIT_TIME = 3L;

    /**
     * 最大重试次数
     */
    int MAX_RETRY_COUNT = 5;

    /**
     * 重试最长忍受时间 30s
     */
    long RETRY_MAX_DURATION_TIME = 30L;

    /**
     * 随机等待的最大时间
     */
    long MAX_RANDOM_WAIT_TIME = 10L;

    /**
     * 指数退避算法中的乘数 2^n * 1000 (ms)
     */
    long EXPONENT_MULTIPLIER = 1000L;

    /**
     * 指数退避最大时间
     */
    long EXPONENT_MAX_WAIT_TIME = 10L;

    /**
     * 默认重试策略
     */
    String DEFAULT_RETRY_STRATEGY = "com.pzt.rpccore.fault.retry.ExponentRetryStrategy";

    /**
     * 默认容错策略
     */
    String DEFAULT_TOLERANT_STRATEGY = "com.pzt.rpccore.fault.tolerant.FailSafeTolerantStrategy";
}
