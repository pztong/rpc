package com.pzt.rpccore.protocol;

import com.pzt.rpccore.constant.ProtocolConstant;
import com.pzt.rpccore.model.RpcRequest;
import com.pzt.rpccore.model.RpcResponse;
import com.pzt.rpccore.serializer.Serializer;
import com.pzt.rpccore.serializer.SerializerAndProtocolFieldMap;
import com.pzt.rpccore.serializer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 自定义协议解码器
 */
@Slf4j
public class Decoder {

    public static Message<?> decode(ByteBuf buf) {
        Message.Header header = new Message.Header();
        header.setMagic(buf.readByte());
        header.setVersion(buf.readByte());
        //解码消息头的第3个byte
        decodeThirdByte(header, buf.readByte());

        header.setStatus(buf.readByte());
        header.setRequestId(buf.readLong());
        int dataLength = buf.readInt();
        if (dataLength < 0) {
            throw new RuntimeException("dataLength < 0, decode error.");
        }
        header.setDataLength(dataLength);
        //解析消息体
        byte[] body = new byte[dataLength];
        buf.readBytes(body);
        //据serializer字段 选择对应的序列化器 反序列化
        byte serializerField = header.getSerializer();
        String serializerName = SerializerAndProtocolFieldMap.getSerializerByField(serializerField);
        Serializer serializer = SerializerFactory.getSerializer(serializerName);
        //客户端使用的是自定义序列化器的话，服务端的配置文件需要指定
        if (serializerField == ProtocolConstant.CUSTOM_SERIALIZER)
            serializer = SerializerFactory.getSerializer();

        try {
            byte isRequest = header.getIsRequest();
            if (isRequest == 1) {
                RpcRequest rpcRequest = serializer.deSerialize(body, RpcRequest.class);
                Message<RpcRequest> rpcRequestMessage = new Message<>();
                rpcRequestMessage.setHeader(header);
                rpcRequestMessage.setBody(rpcRequest);
                return rpcRequestMessage;
            } else if (isRequest == 0) {
                RpcResponse response = serializer.deSerialize(body, RpcResponse.class);
                Message<RpcResponse> rpcResponseMessage = new Message<>();
                rpcResponseMessage.setHeader(header);
                rpcResponseMessage.setBody(response);
                return rpcResponseMessage;
            } else {
                throw new RuntimeException("error message format in req/res.");
            }
        } catch (IOException e) {
            throw new RuntimeException("deSerializer error:" + e.getMessage());
        }
    }

    /**
     * 将字节b 拆开，然后设置header中的 req event serializer extend字段
     *
     * @param header
     * @param b
     */
    private static void decodeThirdByte(Message.Header header, byte b) {
        byte isRequest = (byte) ((b >> 7) & 1);
        byte event = (byte) ((b >> 6) & 1);
        byte serializer = (byte) ((b >> 3) & 7);
        byte extend = (byte) (b & 7);
        header.setIsRequest(isRequest);
        header.setEvent(event);
        header.setSerializer(serializer);
        header.setExtend(extend);
    }
}
