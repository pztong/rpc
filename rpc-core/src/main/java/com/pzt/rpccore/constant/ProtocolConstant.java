package com.pzt.rpccore.constant;

/**
 * 自定义协议相关的常量
 */
public interface ProtocolConstant {

    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH = 20;
    /**
     * 消息头的 magic
     */
    byte MAGIC = (byte) 0b10011011;

    /**
     * 默认协议版本
     */
    byte VERSION = (byte) 1;
    /**
     * 请求
     */
    byte REQUEST = (byte) 1;
    /**
     * 响应
     */
    byte RESPONSE = (byte) 0;

    /**
     * 特殊事件 心跳检测
     */
    byte EVENT_HEART_BEAT = (byte) 0;

    /**
     * 特殊事件 待使用
     */
    byte EVENT_NONE = (byte) 1;

    /**
     * 默认序列化器 json
     * 000：java jdk。001：json。010： hessian。011：Kryo。
     * 4--7：用户自定义序列化器时使用。
     */
    byte DEFAULT_SERIALIZER = (byte) 1;

    /**
     * 自定义序列化器 字段
     */
    byte CUSTOM_SERIALIZER = (byte) 7;
    /**
     * extend扩展字段 暂未使用
     */
    byte DEFAULT_EXTEND = (byte) 0;

    /**
     * 响应状态码
     */
    byte OK = (byte) 20;
    byte BAD_REQUEST = (byte) 40;
    byte SERVICE_NOT_FOUND = (byte) 41;
    byte SERVER_ERROR = (byte) 50;
}
