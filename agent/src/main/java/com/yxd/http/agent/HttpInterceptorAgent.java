package com.yxd.http.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

public class HttpInterceptorAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
//        inst.addTransformer(new HttpTransformer());
//    }
        try {
            JarFileHelper.addJarToBootstrap(inst);
//            String agentJarPath = System.getProperty("java.class.path");
//            System.out.println("MyAgent jar path: " + agentJarPath);

            new AgentBuilder.Default()
                    .type(ElementMatchers.nameContains("HttpURLConnection"))
                    .transform((builder, typeDescription, classLoader, module) ->
                            builder.visit(Advice.to(HttpURLConnectionInterceptor.class).on(ElementMatchers.isMethod())))
                    .installOn(inst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class HttpURLConnectionInterceptor {
        @Advice.OnMethodEnter
        public static void enter(@Advice.Origin Method method, @Advice.AllArguments Object[] args) {
            System.out.println("Enter method: " + method.getName());
        }

        @Advice.OnMethodExit
        public static void exit(@Advice.Origin Method method, @Advice.Return Object ret) {
            System.out.println("Exit method: " + method.getName());
        }
    }
}
