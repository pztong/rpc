package com.pzt.rpccore.server.handler;

import com.pzt.rpccore.protocol.Encoder;
import com.pzt.rpccore.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class Message2BufChannelHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof Message<?>) {
            Message<?> message = (Message<?>) (msg);
            ByteBuf buf = Encoder.encode(message);
            ctx.write(buf,promise);
            return;
        }
        ctx.write(msg, promise);
    }
}
