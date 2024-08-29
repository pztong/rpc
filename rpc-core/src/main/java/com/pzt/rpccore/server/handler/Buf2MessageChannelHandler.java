package com.pzt.rpccore.server.handler;

import com.pzt.rpccore.protocol.Decoder;
import com.pzt.rpccore.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 将缓冲区 ByteBuf 中的字节数据转为 message
 */
public class Buf2MessageChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        int len = buf.readableBytes();
        Message<?> message = Decoder.decode(buf);
        ctx.fireChannelRead(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
