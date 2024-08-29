package com.pzt.rpccore.enums;

import lombok.Getter;

@Getter
public enum EventEnum {
    HEART_BEAT((byte)0,"心跳检测");

    private final byte key;
    private final String event;
    EventEnum(byte key,String event){
        this.key = key;
        this.event = event;
    }


    public static EventEnum getEnumByKey(byte key){
        EventEnum[] values = EventEnum.values();
        for(EventEnum eventEnum:values){
            if(key == eventEnum.getKey())
                return eventEnum;
        }
        return null;
    }


}
