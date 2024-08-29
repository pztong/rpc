package com.pzt.rpccore.server;

import com.pzt.rpccore.protocol.Decoder;
import com.pzt.rpccore.protocol.Message;
import com.pzt.rpccore.server.handler.Buf2MessageChannelHandler;
import com.pzt.rpccore.server.handler.BusinessServerChannelHandler;
import com.pzt.rpccore.server.handler.Message2BufChannelHandler;
import com.pzt.rpccore.server.handler.ValidInChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * 使用自定义协议的服务器端
 */
@Slf4j
public class MyProtocolServer implements Server {
    private ChannelFuture f;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Override
    public void doStart(int port) {
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            //入站处理器
                            p.addLast(new ValidInChannelHandler());
                            p.addLast(new Buf2MessageChannelHandler());//入
                            p.addLast(new BusinessServerChannelHandler());//入
                            //出站处理器
                            p.addLast(new Message2BufChannelHandler());//出
                        }
                    });

            this.f = b.bind(port).sync();
            System.out.println("Server started on port: " + port);
//            f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public void doStop() {
        try {
            f.channel().close().sync();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (bossGroup != null)
                bossGroup.shutdownGracefully();
            if(workerGroup!= null)
                workerGroup.shutdownGracefully();
        }

    }

}
