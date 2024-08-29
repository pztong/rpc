package com.pzt.pztrpcspringbootstarter.bootstrap;

import com.pzt.pztrpcspringbootstarter.anotation.PztRpcEnable;
import com.pzt.rpccore.RpcApplication;
import com.pzt.rpccore.server.MyProtocolServer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;


/**
 * 框架初始化
 */
public class RpcBootStrap implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        MultiValueMap<String, Object> attributes = importingClassMetadata.getAllAnnotationAttributes(PztRpcEnable.class.getName());
        String key = "needServer";
        if (attributes != null && attributes.containsKey(key))  {
            boolean needServer = (boolean) attributes.get(key).get(0);
            //初始化
            RpcApplication.init();
            if(needServer){
                MyProtocolServer server = new MyProtocolServer();
                server.doStart(RpcApplication.getRpcConfig().getServerPort());
            }
        }

    }

}
