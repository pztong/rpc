package com.pzt.rpccore.server.handler;

import com.pzt.rpccore.constant.ProtocolConstant;
import com.pzt.rpccore.exception.BusinessException;
import com.pzt.rpccore.model.RpcRequest;
import com.pzt.rpccore.model.RpcResponse;
import com.pzt.rpccore.protocol.Message;
import com.pzt.rpccore.registry.LocalRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class BusinessServerChannelHandler extends SimpleChannelInboundHandler<Message<RpcRequest>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message<RpcRequest> message) throws Exception {
        Message.Header header = message.getHeader();
        //req
        if (header.getIsRequest() == 1) {
            RpcRequest rpcRequest = message.getBody();
            //构造响应对象
            RpcResponse rpcResponse = new RpcResponse();
            try {
                if (rpcRequest == null) {
                    header.setStatus(ProtocolConstant.BAD_REQUEST);
                    throw new RuntimeException("The request is null.");
                }

                //找到服务 并执行
                Class<?> impClass = LocalRegistry.get(rpcRequest.getServiceName(), rpcRequest.getVersion());
                if (impClass == null) {
                    header.setStatus(ProtocolConstant.SERVICE_NOT_FOUND);
                    throw new RuntimeException("The service is not found.");
                }
                //利用反射
                Method method = impClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(impClass.getDeclaredConstructor().newInstance(), rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
                header.setStatus(ProtocolConstant.OK);
            } catch (Exception e) {
                BusinessException exception = new BusinessException(e.getMessage());
                rpcResponse.setException(exception);
                rpcResponse.setMessage(exception.getMessage());
            }
            doResponse(ctx, header, rpcResponse);

        }
    }

    private void doResponse(ChannelHandlerContext ctx, Message.Header header, RpcResponse body) {
        Message<RpcResponse> responseMessage = new Message<>();
        header.setIsRequest(ProtocolConstant.RESPONSE);
        responseMessage.setHeader(header);
        responseMessage.setBody(body);
        ctx.channel().writeAndFlush(responseMessage);
    }
}
