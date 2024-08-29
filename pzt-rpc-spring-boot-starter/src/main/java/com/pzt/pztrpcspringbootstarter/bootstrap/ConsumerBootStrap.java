package com.pzt.pztrpcspringbootstarter.bootstrap;

import com.pzt.pztrpcspringbootstarter.anotation.PztRpcReference;
import com.pzt.rpccore.model.ServiceMetaInfo;
import com.pzt.rpccore.proxy.ServiceProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * 服务消费者 初始化
 */
public class ConsumerBootStrap implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for(Field field : fields){
            PztRpcReference annotation = field.getAnnotation(PztRpcReference.class);
            if(annotation != null){
                Class<?> aClass = annotation.interfaceClass();
                if(aClass == void.class){
                    aClass = field.getType();
                }

                Object proxy = ServiceProxyFactory.getProxy(aClass);
                field.setAccessible(true);

                try {
                    field.set(bean,proxy);
                    field.setAccessible(false);
                }catch (Exception e){
                    throw new RuntimeException("为字段注入代理失败。");
                }

            }
        }

        return bean;
    }
}
