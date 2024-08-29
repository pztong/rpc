package com.pzt.rpccore.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义协议的消息格式
 * @param <T>
 */
@Data
public class Message<T> {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体 （请求和响应对象）
     */
    private T body;



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Header {
        /**
         *魔术
         */
        private byte magic;
        /**
         * 协议版本
         */
        private byte version;
        /**
         * 请求 or 响应
         */
        private byte isRequest;
        /**
         * 特殊事件
         */
        private byte event;
        /**
         *
         * 使用的序列化方式
         */
        private byte serializer;
        /**
         * 扩展位
         */
        private byte extend;
        /**
         * 响应状态  请在消息为响应时使用
         */
        private byte status;
        /**
         * 请求id
         */
        private long requestId;
        /**
         * 消息体长度
         */
        private int dataLength;
    }

}
