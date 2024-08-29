package com.pzt.rpccore.registry;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地注册中心  根据  服务名  找到对应的  实现类
 */
public class LocalRegistry {
    private final static Map<String, Class<?>> map = new HashMap<>();

    /**
     * 服务注册
     *
     * @param serviceName
     * @param version
     * @param serviceClass
     */
    public static void register(String serviceName, String version, Class<?> serviceClass) {
        String key = serviceName + ":" + version;
        map.put(key, serviceClass);
    }

    /**
     * 根据服务名获取实现类
     *
     * @param serviceName
     * @param version
     * @return
     */
    public static Class<?> get(String serviceName, String version) {

        return map.get(serviceName + ":" + version);
    }

    /**
     * 移除某个服务
     *
     * @param serviceName
     * @return
     */
    public static Object remove(String serviceName) {
        return map.remove(serviceName);
    }
}
