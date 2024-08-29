package com.pzt.pztrpcspringbootstarter.anotation;

import com.pzt.rpccore.constant.RpcConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务消费者使用的注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PztRpcReference {

    /**
     * 服务接口类
     */
    Class<?> interfaceClass() default void.class;


    /**
     * 服务版本
     *
     * @return
     */
    String version() default RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 是否启用模拟接口
     *
     * @return
     */
    boolean mock() default false;
}
