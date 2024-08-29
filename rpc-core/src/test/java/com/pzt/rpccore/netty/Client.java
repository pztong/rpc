package com.pzt.rpccore.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
//                            ch.pipeline().addLast(new StringEncoder());
                        }
                    });

            ChannelFuture f = bootstrap.connect("localhost", 8888).sync();
            String msg = "Message~~~~Message~~~~";
            // 模拟短时间内发送多个请求
            for (int i = 1; i < 100; i++) {
                ByteBuf byteBuf = Unpooled.copiedBuffer((msg + i).getBytes());
                f.channel().writeAndFlush(byteBuf);
                Thread.sleep(1000);
            }

            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
