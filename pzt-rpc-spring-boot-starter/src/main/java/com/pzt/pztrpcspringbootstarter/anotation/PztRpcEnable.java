package com.pzt.pztrpcspringbootstarter.anotation;

import com.pzt.pztrpcspringbootstarter.bootstrap.ConsumerBootStrap;
import com.pzt.pztrpcspringbootstarter.bootstrap.ProviderBootStrap;
import com.pzt.pztrpcspringbootstarter.bootstrap.RpcBootStrap;
import org.springframework.context.annotation.Import;

import javax.annotation.concurrent.Immutable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用PztRpc框架
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcBootStrap.class, ProviderBootStrap.class, ConsumerBootStrap.class})
public @interface PztRpcEnable {

    /**
     * 是否需要启动web服务
     * consumer：false
     * provider：true
     * @return
     */
    boolean needServer() default true;
}
