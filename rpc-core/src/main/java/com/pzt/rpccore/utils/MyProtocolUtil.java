package com.pzt.rpccore.utils;

import com.pzt.rpccore.RpcApplication;
import com.pzt.rpccore.constant.ProtocolConstant;
import com.pzt.rpccore.model.RpcRequest;
import com.pzt.rpccore.protocol.Message;
import com.pzt.rpccore.serializer.SerializerAndProtocolFieldMap;

import java.util.Random;

public class MyProtocolUtil {

    public static Message<RpcRequest> request2message(RpcRequest request) {
        Message<RpcRequest> message = new Message<>();
        Message.Header header = new Message.Header();

        header.setMagic(ProtocolConstant.MAGIC);
        header.setVersion(ProtocolConstant.VERSION);
        header.setIsRequest(ProtocolConstant.REQUEST);
        header.setEvent(ProtocolConstant.EVENT_NONE);
        String serializer = RpcApplication.getRpcConfig().getSerializer();
        header.setSerializer(SerializerAndProtocolFieldMap.getFieldBySerializer(serializer));
        header.setExtend(ProtocolConstant.DEFAULT_EXTEND);
        //请求报文的status字段 没有用  不设置了
        header.setRequestId(new Random().nextLong());
        //data length字段暂不设置 需要序列化后才知道多长，在encoder中实现

        message.setHeader(header);
        message.setBody(request);
        return message;
    }


}
