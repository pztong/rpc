package com.pzt.rpccore.serializer;

import com.pzt.rpccore.RpcApplication;
import com.pzt.rpccore.constant.RpcConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化器工厂
 */
@Slf4j
public class SerializerFactory {
    /**
     * 序列化器map key:序列化器名称 value:序列化器
     */
    private static volatile Map<String, Serializer> serializerMap;

    /**
     * 返回配置文件指定的序列化器
     * @return
     */
    public static Serializer getSerializer() {
        String serializerName = RpcApplication.getRpcConfig().getSerializer();
        return getSerializer(serializerName);
    }

    public static Serializer getSerializer(String serializerName) {
        if(serializerMap == null){
            synchronized (SerializerFactory.class){
                if(serializerMap == null){
                    loadAllSerializer();
                }
            }
        }
        if(!serializerMap.containsKey(serializerName)){
            log.info("The serializer of {} is not existent. Return the default serializer.",serializerName);
            return serializerMap.get(RpcConstant.DEFAULT_SERIALIZER);
        }
        return serializerMap.get(serializerName);
    }

    /**
     * 加载 META-INF/services/ 下的所有序列号器实现类
     */
    private static void loadAllSerializer() {
        ConcurrentHashMap<String, Serializer> concurrentHashMap = new ConcurrentHashMap<>();
        ServiceLoader<Serializer> loader = ServiceLoader.load(Serializer.class);
        for (Serializer serializer : loader) {
            concurrentHashMap.put(serializer.getClass().getName(), serializer);
        }
        serializerMap = concurrentHashMap;
    }


}
