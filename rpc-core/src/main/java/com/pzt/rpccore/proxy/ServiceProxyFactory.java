package com.pzt.rpccore.proxy;

import com.pzt.rpccore.RpcApplication;
import com.pzt.rpccore.config.RpcConfig;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂
 */
public class ServiceProxyFactory {
    private static final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

    public static <T> T getProxy(Class<T> serviceClass) {
        if (!serviceClass.isInterface()) {
            throw new IllegalArgumentException("The given class must be an interface: " + serviceClass.getName());
        }

        // 创建模拟代理对象
        if (rpcConfig.isMock())
            return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                    new Class<?>[]{serviceClass},
                    new MockInvocationHandler()
            );


        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass},
                new ServiceInvocationHandler()
        );
    }


}
