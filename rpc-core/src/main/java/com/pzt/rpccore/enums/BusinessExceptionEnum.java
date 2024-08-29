package com.pzt.rpccore.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BusinessExceptionEnum {
    PARAMS_ERROR(40000,"参数格式错误!"),

    ;

    private final int code;
    private final String msg;
}
