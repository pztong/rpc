package com.pzt.pztrpcspringbootstarter.bootstrap;

import com.pzt.pztrpcspringbootstarter.anotation.PztRpcService;
import com.pzt.rpccore.RpcApplication;
import com.pzt.rpccore.config.RpcConfig;
import com.pzt.rpccore.model.ServiceMetaInfo;
import com.pzt.rpccore.registry.LocalRegistry;
import com.pzt.rpccore.registry.RegistryFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * 提供者初始化
 */
public class ProviderBootStrap implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        PztRpcService annotation = beanClass.getAnnotation(PztRpcService.class);
        if (annotation != null) {
            String serviceName = null;
            if (annotation.interfaceClass() != void.class) {
                serviceName = annotation.interfaceClass().getName();
            } else {
                Class<?>[] interfaces = beanClass.getInterfaces();
                if (interfaces.length > 0) {
                    serviceName = interfaces[0].getName();
                }
            }
            if (serviceName != null) {
                String version = annotation.version();
                //本地注册
                LocalRegistry.register(serviceName, version, beanClass);
                //注册中心注册
                RpcConfig rpcConfig = RpcApplication.getRpcConfig();
                ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                        .serviceName(serviceName)
                        .version(version)
                        .host(rpcConfig.getServerHost())
                        .port(rpcConfig.getServerPort())
                        .build();
                try {
                    RegistryFactory.getRegistry().registryService(serviceMetaInfo);
                } catch (Exception e) {
                    throw new RuntimeException("服务注册失败:" + e.getMessage());
                }

            }
        }
        return bean;
    }
}
