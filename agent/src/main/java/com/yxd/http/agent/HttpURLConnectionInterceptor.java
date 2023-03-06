package com.yxd.http.agent;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class HttpURLConnectionInterceptor {
    @Advice.OnMethodEnter
    public static void enter(@Advice.Origin Method method, @Advice.AllArguments Object[] args) {
        System.out.println("Enter method: " + method.getName());
    }

    @Advice.OnMethodExit
    public static void exit(@Advice.Origin Method method, @Advice.Return Object ret) {
        System.out.println("Exit method: " + method.getName());
    }
}