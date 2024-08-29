package com.pzt.rpccore.serializer;

import java.io.IOException;

/**
 * 序列化器接口
 */
public interface Serializer {

    <T> byte[] serialize(T object) throws IOException;

    <T> T deSerialize(byte[] bytes,Class<T> type) throws IOException;
}
