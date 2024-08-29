package com.pzt.rpccore.model;

import com.pzt.rpccore.server.Client;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClientLRUCache extends LinkedHashMap<String, Client> {
    private final int capacity;

    public ClientLRUCache(int capacity){
        super(capacity * 2,0.75f,true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String,Client> eldest) {
        if(size() > capacity){
            eldest.getValue().close();
            return true;
        }
        return false;
    }

}
