package com.pzt.rpccore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * rpc 请求封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcRequest implements Serializable {
    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 服务版本
     */
    private String version;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 方法参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 方法参数列表
     */
    private Object[] args;

}
