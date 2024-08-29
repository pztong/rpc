package com.pzt.rpccore.server;

import com.pzt.rpccore.model.RpcRequest;
import com.pzt.rpccore.model.RpcResponse;
import com.pzt.rpccore.registry.LocalRegistry;
import com.pzt.rpccore.serializer.Serializer;
import com.pzt.rpccore.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

@Slf4j
public class VertxHttpServerHandler implements Handler<HttpServerRequest> { ;
    private final Serializer serializer = SerializerFactory.getSerializer();
    @Override
    public void handle(HttpServerRequest request) {
        log.info("Received request:" + request.method() + " " + request.uri());
        request.bodyHandler(body -> {
            byte[] bodyBytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deSerialize(bodyBytes, RpcRequest.class);
            } catch (IOException e) {
                log.error("deserialize request error", e);
            }
            //构造响应对象
            RpcResponse rpcResponse = new RpcResponse();
            if(rpcRequest == null){
                log.info("The request is null.");
                rpcResponse.setMessage("The request is null.");
                this.doResponse(request, rpcResponse, serializer);
                return;
            }
            //找到服务 并执行
            Class<?> impClass = LocalRegistry.get(rpcRequest.getServiceName(),rpcRequest.getVersion());
            if(impClass == null){
                log.info("The service is not found.");
                rpcResponse.setMessage("The service is not found.");
                this.doResponse(request, rpcResponse, serializer);
                return;
            }
            try {
                Method method = impClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(impClass.getDeclaredConstructor().newInstance(), rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            }catch (Exception e){
                log.error("invoke error", e);
                rpcResponse.setException(e);
                rpcResponse.setMessage(e.getMessage());
            }
            this.doResponse(request, rpcResponse, serializer);
        });

    }

    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer){
        HttpServerResponse response = request.response();
        response.putHeader("Content-Type", "application/json");

        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            request.response().end(Buffer.buffer(serialized));
        }catch (IOException e){
            log.error("serialize response error", e);
            request.response().end(Buffer.buffer());
        }

    }

}
