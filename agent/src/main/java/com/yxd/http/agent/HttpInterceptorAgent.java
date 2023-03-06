package com.yxd.http.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

public class HttpInterceptorAgent {
    public static void premain(String agentArgs, Instrumentation inst) {

        try {
            JarFileHelper.addJarToBootstrap(inst);
//            Class[] allLoadedClasses = inst.getAllLoadedClasses();
//            for (Class allLoadedClass : allLoadedClasses) {
//                System.out.println(allLoadedClass.getName());
//            }
//            new AgentBuilder.Default()
//                    .type(ElementMatchers.nameContains("HttpURLConnection"))
//                    .transform(new MyTransformer())
//                    .installOn(inst);

            inst.addTransformer(new MyTransformer1(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
