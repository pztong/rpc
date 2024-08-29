package com.pzt.rpccore.protocol;

import com.pzt.rpccore.constant.ProtocolConstant;
import com.pzt.rpccore.serializer.Serializer;
import com.pzt.rpccore.serializer.SerializerAndProtocolFieldMap;
import com.pzt.rpccore.serializer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 自定义协议编码器
 */
@Slf4j
public class Encoder {

    public static ByteBuf encode(Message<?> message) {
        ByteBuf buf = Unpooled.buffer();

        Message.Header header = message.getHeader();
        buf.writeByte(header.getMagic());
        buf.writeByte(header.getVersion());
        // 将req/res event serializer  extend 合并成一个byte
        buf.writeByte(generateThirdByte(header));

        buf.writeByte(header.getStatus());
        buf.writeLong(header.getRequestId());

        //消息体 RpcRequest or RpcResponse  序列化

        try {
            //根据serializer字段选择对应的序列化器
            byte serializerField = header.getSerializer();
            String serializerName = SerializerAndProtocolFieldMap.getSerializerByField(serializerField);
            Serializer serializer = SerializerFactory.getSerializer(serializerName);

            byte[] serializedBytes = serializer.serialize(message.getBody());
            //序列化后才知道数据部分的长度
            header.setDataLength(serializedBytes.length);
            buf.writeInt(serializedBytes.length);
            buf.writeBytes(serializedBytes);
        } catch (IOException e) {
            log.error("serialize body error:{}", e.getMessage());
            return null;
        }
        return buf;
    }

    /**
     * 将req/res event serializer  extend 合并成一个byte
     *
     * @param header
     * @return
     */
    private static byte generateThirdByte(Message.Header header) {
        // req/res 1bit event 1bit   serializer 3bit extend 3bit
        byte res = (byte) 0;
        if (header.getIsRequest() == 1) {
            res = (byte) (res | 1 << 7);
        }
        if (header.getEvent() == 1)
            res = (byte) (res | 1 << 6);
        byte serializer = (byte) (header.getSerializer() & 7);
        res = (byte) (res | serializer << 3);

        byte extend = (byte) (header.getExtend() & 7);
        res = (byte) (res | extend);

        return res;
    }

}
