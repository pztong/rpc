package com.pzt.rpccore.server;

import com.pzt.rpccore.model.RpcRequest;
import com.pzt.rpccore.model.RpcResponse;
import com.pzt.rpccore.protocol.Encoder;
import com.pzt.rpccore.protocol.Message;
import com.pzt.rpccore.server.handler.Buf2MessageChannelHandler;
import com.pzt.rpccore.server.handler.Message2BufChannelHandler;
import com.pzt.rpccore.server.handler.ValidInChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 使用自定义协议的客户端
 */
@Slf4j
public class MyProtocolClient implements Client {
    private static final ConcurrentHashMap<Long, CompletableFuture<RpcResponse>> responseMap = new ConcurrentHashMap<>();
    private final String host;
    private final int port;
    private ChannelFuture f;
    private EventLoopGroup group;

    public MyProtocolClient(String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    private void init() {
        this.group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            //入站处理器
                            p.addLast(new ValidInChannelHandler());
                            p.addLast(new Buf2MessageChannelHandler());
                            p.addLast(new BusinessClientChannelHandler());
                            //出站处理器
                            p.addLast(new Message2BufChannelHandler());

                        }
                    });
            this.f = b.connect(host, port).sync();
        } catch (Exception e) {
            log.error(e.getMessage());
            if(this.group != null)
                this.group.shutdownGracefully();
        }
    }


    @Override
    public RpcResponse send(Message<RpcRequest> requestMessage) {
        try {
            long requestId = requestMessage.getHeader().getRequestId();
            CompletableFuture<RpcResponse> completableFuture = new CompletableFuture<>();
            responseMap.put(requestId, completableFuture);
            //写入通道 发送
            f.channel().writeAndFlush(requestMessage);
            return completableFuture.get();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public CompletableFuture<RpcResponse> sendAsync(Message<RpcRequest> requestMessage) {
        try {
            long requestId = requestMessage.getHeader().getRequestId();
            CompletableFuture<RpcResponse> completableFuture = new CompletableFuture<>();
            responseMap.put(requestId, completableFuture);
            //写入通道 发送
            f.channel().writeAndFlush(requestMessage).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(!channelFuture.isSuccess()){
                        completableFuture.completeExceptionally(channelFuture.cause().getCause());
                    }
                }
            });
            return completableFuture;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public void close() {
        try {
            f.channel().close().sync();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (this.group != null)
                this.group.shutdownGracefully();
        }

    }

    @Override
    public boolean isAlive() {
        return this.f.channel().isActive();
    }

    public static class BusinessClientChannelHandler extends SimpleChannelInboundHandler<Message<RpcResponse>> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Message<RpcResponse> message) throws Exception {
            long requestId = message.getHeader().getRequestId();
            CompletableFuture<RpcResponse> future = MyProtocolClient.responseMap.remove(requestId);
            if (future != null) {
                future.complete(message.getBody());
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
