package com.pzt.rpccore.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class MockInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        log.info("invoke mock method:{}",method.getName());
        return generateMockRes(returnType);
    }

    public <T> Object generateMockRes(Class<T> resType) {
        if (resType.isPrimitive()) {
            if (resType == boolean.class)
                return true;
            else if (resType == byte.class)
                return (byte) 0;
            else if (resType == short.class)
                return (short) 0;
            else if (resType == int.class)
                return 0;
            else if (resType == long.class)
                return 0L;
            else if (resType == float.class)
                return (float)0;
            else if (resType == double.class)
                return (double)0;
            else if (resType == char.class)
                return '0';
        }
        return null;
    }

}
