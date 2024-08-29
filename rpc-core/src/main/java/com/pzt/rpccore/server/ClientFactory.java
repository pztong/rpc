package com.pzt.rpccore.server;

import com.pzt.rpccore.constant.RpcConstant;
import com.pzt.rpccore.model.ClientLRUCache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端工厂
 */
public class ClientFactory {
    //继承了 LinkedHashMap  很方便的使用LRU策略淘汰（关闭）不用的client
    private static final ClientLRUCache clientMap = new ClientLRUCache(RpcConstant.CLIENT_KEEP_ALIVE_MAX_NUMBER);
    /**
     * 根据服务器的host port 返回对应的client
     * @param host
     * @param port
     * @return
     */
    public static Client getClient(String host,int port){
        String key = host + ":" + port;
        Client client = clientMap.get(key);
        if(client != null && client.isAlive() )
            return client;
        //不存在，则创建
        client = new MyProtocolClient(host, port);
        clientMap.put(key,client);
        return client;
    }

    /**
     * 关闭所有客户端
     */
    public static void closeAllClient(){
        Collection<Client> values = clientMap.values();
        for(Client client : values)
            client.close();
    }
}
