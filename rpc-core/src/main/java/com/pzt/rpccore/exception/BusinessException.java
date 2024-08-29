package com.pzt.rpccore.exception;

import lombok.ToString;

/**
 * 自定义业务异常
 */
public class BusinessException extends RuntimeException {
    private final String msg;

    public BusinessException(String msg) {
        super(msg);
        this.msg = msg;
        this.setStackTrace(new StackTraceElement[0]);
    }

    @Override
    public String toString() {
        return "BusinessException:" + msg;
    }
}
