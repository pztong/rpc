package com.pzt.rpccore.server.handler;

import com.pzt.rpccore.constant.ProtocolConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 校验 避免乱七八糟的请求
 */
public class ValidInChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        int len = byteBuf.readableBytes();
        if(len < 2){
            ctx.fireChannelReadComplete();
        }
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);

        //magic 和 version不匹配的直接pass
        byte magic = bytes[0];
        byte version = bytes[1];
        if(ProtocolConstant.MAGIC != magic || ProtocolConstant.VERSION != version){
            ctx.fireChannelReadComplete();
        }
        //校验通过放行
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        ctx.fireChannelRead(buf);

    }



}
