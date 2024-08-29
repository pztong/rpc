package com.pzt.rpccore.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class NettyTest {

    @Test
    void byteBufTest() {
        ByteBuf buf = Unpooled.buffer(10);
        for (int i = 1; i <= 10; i++) {
            buf.writeByte(i);
        }
        byte a = buf.readByte();
        System.out.println("a = " + a);
        byte[] bytes = new byte[4];
        ByteBuf newBuf = buf.readBytes(bytes);

        System.out.println(Arrays.toString(bytes));

        byte b = buf.readByte();
        System.out.println(b);
        System.out.println("over");
        // 写入数据
//        buf.writeByte(999999);
//        buf.writeInt(Integer.MAX_VALUE);
//        buf.writeInt(-2);

        // 读取数据
//        int a = buf.readByte();
//        int b = buf.readInt();

//        byte c = buf.getByte(4);

//        System.out.println("a = " + a); // 输出: a = 1
//        System.out.println("b = " + b); // 输出: b = 100
//        System.out.println("c = " + c); // 输出: b = 100

//        ByteBuf buf2 = Unpooled.buffer(10);
//        int h = 0b10110010;
//        System.out.println(Integer.toBinaryString(h));
    }
}
