package com.pzt.rpccore;

import com.pzt.rpccore.model.RpcRequest;
import com.pzt.rpccore.protocol.Decoder;
import com.pzt.rpccore.protocol.Encoder;
import com.pzt.rpccore.protocol.Message;
import com.pzt.rpccore.server.MyProtocolClient;
import com.pzt.rpccore.server.MyProtocolServer;
import com.pzt.rpccore.utils.MyProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

@Slf4j
public class ServerTest {

    @Test
    void byteTest() {
        byte b = (byte) 1;
        byte c = (byte) (b << 2);
        System.out.println(b);
        System.out.println(c);
    }

    @Test
    void encoderDecoderTest() {

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName("userService")
                .version("1.1.1")
                .methodName("getName")
                .build();
        Message<RpcRequest> oldMessage = MyProtocolUtil.request2message(rpcRequest);

        ByteBuf buf1 = Encoder.encode(oldMessage);

        Message<?> newMessage = Decoder.decode(buf1);
        System.out.println("old message:" + oldMessage);
        System.out.println("---------------------------");
        System.out.println("new newMessage:" + newMessage);

        System.out.println("---------------------------");
    }


    @Test
    void serverTest() {
        int port = 8888;
        MyProtocolServer server = new MyProtocolServer();

        server.doStart(port);
        System.out.println("over server");
    }

    @Test
    void clientTest() {
        String host = "localhost";
        int port = 8888;
        MyProtocolClient client = new MyProtocolClient(host, port);
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName("userService")
                .version("1.1.1")
                .methodName("getName")
                .build();
        RpcRequest request = new RpcRequest();
        Message<RpcRequest> message = MyProtocolUtil.request2message(rpcRequest);

        try {
            for (int i = 0; i < 100; i++){
                client.send(message);
                if(i % 10 == 0)
                    Thread.sleep(1000);
            }

            Thread.sleep(1000000);
            System.out.println("还连接吗？---" + client.isAlive());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("over client");
    }
}
