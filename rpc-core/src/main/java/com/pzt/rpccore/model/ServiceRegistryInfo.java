package com.pzt.rpccore.model;

import com.pzt.rpccore.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册服务所需要的信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRegistryInfo<T> {
    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 版本
     */
    private String version = RpcConstant.DEFAULT_SERVICE_VERSION;
    /**
     * 实现类
     */
    private Class<? extends T> implClass;
}
