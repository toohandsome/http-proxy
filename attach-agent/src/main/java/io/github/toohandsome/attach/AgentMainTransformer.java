package io.github.toohandsome.attach;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class AgentMainTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        className = className.replace("/", ".");
        if ("demo.controller.Tc1".equals(className)) {
            System.out.println("=======================================");
            try {
                ClassPool classPool = ClassPool.getDefault();
                CtClass ctClass = classPool.get("demo.controller.Tc1");
                CtMethod[] declaredMethods = ctClass.getDeclaredMethods();

                for (CtMethod method : declaredMethods) {
                    // 修改方法体来实现， 增加两个局部变量用于记录执行时间
                    method.addLocalVariable("startTimeAgent", CtClass.longType);
                    method.insertBefore("startTimeAgent = System.currentTimeMillis();");
                    method.addLocalVariable("methodNameAgent", classPool.get(String.class.getName()));
                    method.insertBefore("methodNameAgent = \"" + method.getLongName() + "\";");
                    method.insertAfter("System.out.println(methodNameAgent + \" exec time is :\" + (System.currentTimeMillis() - startTimeAgent) + \"ms\");");
                }

                return ctClass.toBytecode();
                //detach的意思是将内存中曾经被javassist加载过的Test对象移除，如果下次有需要在内存中找不到会重新走javassist加载
//                ctClass.detach();


            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("============"+e.getMessage());
                return classfileBuffer;
            }
        } else {
            return classfileBuffer;
        }


    }


}