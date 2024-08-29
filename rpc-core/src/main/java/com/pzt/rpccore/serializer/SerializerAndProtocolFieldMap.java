package com.pzt.rpccore.serializer;

import com.pzt.rpccore.constant.ProtocolConstant;
import com.pzt.rpccore.constant.RpcConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器 与 协议中的序列化器的映射
 */
public class SerializerAndProtocolFieldMap {
    private static final Map<String, Byte> serializer2filedMap = new HashMap<>();
    private static final Map<Byte, String> filed2serializerMap = new HashMap<>();

    static {
        serializer2filedMap.put(JdkSerializer.class.getName(), (byte) 0);
        serializer2filedMap.put(JsonSerializer.class.getName(), (byte) 1);
        serializer2filedMap.put(HessianSerializer.class.getName(), (byte) 2);
        serializer2filedMap.put(KryoSerializer.class.getName(), (byte) 3);

        filed2serializerMap.put((byte) 0,JdkSerializer.class.getName());
        filed2serializerMap.put((byte) 1,JsonSerializer.class.getName());
        filed2serializerMap.put((byte) 2,HessianSerializer.class.getName());
        filed2serializerMap.put((byte) 3,KryoSerializer.class.getName());
    }

    public static byte getFieldBySerializer(String serializerName){
        return serializer2filedMap.getOrDefault(serializerName,ProtocolConstant.DEFAULT_SERIALIZER);
    }

    public static boolean containsSerializer(String serializeName){
        return serializer2filedMap.containsKey(serializeName);
    }

    public static String getSerializerByField(byte field){
        return filed2serializerMap.getOrDefault(field,RpcConstant.DEFAULT_SERIALIZER);
    }

}
