package com.pzt.pztrpcspringbootstarter.anotation;

import com.pzt.rpccore.constant.RpcConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务提供者使用的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PztRpcService {

    /**
     * 服务接口类
     * 默认为实现的第一个接口
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务版本
     *
     * @return
     */
    String version() default RpcConstant.DEFAULT_SERVICE_VERSION;
}
