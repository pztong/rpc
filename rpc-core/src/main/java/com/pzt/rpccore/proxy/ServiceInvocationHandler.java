package com.pzt.rpccore.proxy;

import com.pzt.rpccore.constant.RpcConstant;
import com.pzt.rpccore.fault.retry.RetryStrategy;
import com.pzt.rpccore.fault.retry.RetryStrategyFactory;
import com.pzt.rpccore.fault.tolerant.TolerantStrategy;
import com.pzt.rpccore.fault.tolerant.TolerantStrategyFactory;
import com.pzt.rpccore.loadbalance.LoaderBalance;
import com.pzt.rpccore.loadbalance.LoaderBalanceFactory;
import com.pzt.rpccore.model.RpcRequest;
import com.pzt.rpccore.model.RpcResponse;
import com.pzt.rpccore.model.ServiceMetaInfo;
import com.pzt.rpccore.protocol.Message;
import com.pzt.rpccore.registry.Registry;
import com.pzt.rpccore.registry.RegistryFactory;
import com.pzt.rpccore.server.Client;
import com.pzt.rpccore.server.ClientFactory;
import com.pzt.rpccore.utils.MyProtocolUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public class ServiceInvocationHandler implements InvocationHandler {
    private final Registry registry = RegistryFactory.getRegistry();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构建服务元信息
        //todo 默认都是1.0.0版本
        String serviceName = method.getDeclaringClass().getName();
        String version = RpcConstant.DEFAULT_SERVICE_VERSION;
        String methodName = method.getName();
        ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                .serviceName(serviceName)
                .version(version)
                .build();
        //从注册中心查找服务提供者
        List<ServiceMetaInfo> serviceMetaInfoList = registry.findService(serviceMetaInfo);
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("methodName", methodName);
        paramsMap.put("args", Arrays.toString(args));
        ServiceMetaInfo providerInfo = this.doSelectProvider(paramsMap, serviceMetaInfoList);
        if (providerInfo == null)
            throw new RuntimeException("Don't find any service provider.");
        //构造请求  发送
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .version(version)
                .methodName(methodName)
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            Message<RpcRequest> requestMessage = MyProtocolUtil.request2message(rpcRequest);
            Client client = ClientFactory.getClient(providerInfo.getHost(), providerInfo.getPort());
            //找服务提供者提供服务，增加重试策略
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance();
            RpcResponse rpcResponse = retryStrategy.call(new Callable<RpcResponse>() {
                @Override
                public RpcResponse call() throws Exception {
                    RpcResponse rpcResponse1 = client.send(requestMessage);
                    return rpcResponse1;
                }
            });
            return rpcResponse.getData();
        } catch (Exception e) {
            HashMap<String, Object> context = new HashMap<>();
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance();
            return tolerantStrategy.doTolerant(context, e);
        }
    }

    /**
     * 从多个服务提供者中选择的策略
     *
     * @param requestParams
     * @param serviceMetaInfoList
     * @return
     */
    private ServiceMetaInfo doSelectProvider(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList == null || serviceMetaInfoList.isEmpty()) {
            return null;
        }
        LoaderBalance loaderBalance = LoaderBalanceFactory.getInstance();
        return loaderBalance.select(requestParams, serviceMetaInfoList);
    }
}
