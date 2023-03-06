package io.github.toohandsome.proxy.attach;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class MyTransformer1 implements ClassFileTransformer {


    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

//        System.out.println(className);
        //java/net/HttpURLConnection
        if (className.equals("java/net/HttpURLConnection")) {
            try {
                //借助JavaAssist工具，进行字节码插桩
                ClassPool pool = ClassPool.getDefault();
                CtClass cc = pool.get("java.net.HttpURLConnection");
                CtMethod personFly = cc.getDeclaredMethod("setRequestMethod");

                //在目标方法前后，插入代码
                personFly.insertBefore("System.out.println(\"--- before setRequestMethod ---\");");
                personFly.insertAfter("System.out.println(\"--- after setRequestMethod ---\");");

                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (className.equals("sun/net/www/protocol/http/HttpURLConnection")) {
            try {
                //借助JavaAssist工具，进行字节码插桩
                ClassPool pool = ClassPool.getDefault();
                CtClass cc = pool.get("sun.net.www.protocol.http.HttpURLConnection");
                CtMethod personFly = cc.getDeclaredMethod("getOutputStream");

                //在目标方法前后，插入代码
                personFly.insertBefore("System.out.println(\"--- before getOutputStream ---\");");
                personFly.insertAfter("System.out.println(\"--- after getOutputStream ---\");");

                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;



    }
}
