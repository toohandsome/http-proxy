package io.github.toohandsome.attach;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import sun.net.www.protocol.http.HttpURLConnection;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.ProtectionDomain;

public class MyTransformer1 implements ClassFileTransformer {


    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

//        System.out.println(className);
        //java/net/HttpURLConnection
//        if (className.equals("java/net/HttpURLConnection")) {
//            try {
//                //借助JavaAssist工具，进行字节码插桩
//                ClassPool pool = ClassPool.getDefault();
//                CtClass cc = pool.get("java.net.HttpURLConnection");
//                CtMethod personFly = cc.getDeclaredMethod("setRequestMethod");
//
//                //在目标方法前后，插入代码
////                personFly.insertBefore("System.out.println(\"--- before setRequestMethod ---\");");
////                personFly.insertAfter("System.out.println(\"--- after setRequestMethod ---\");");
//
//                return cc.toBytecode();
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        } else
//        if (className.equals("sun/net/www/protocol/http/HttpURLConnection")) {
//            try {
//                //借助JavaAssist工具，进行字节码插桩
//                ClassPool pool = ClassPool.getDefault();
//                pool.importPackage("io.github.toohandsome.attach.util");
////                pool.importPackage("sun.net.www.protocol.http");
//                CtClass cc = pool.get("sun.net.www.protocol.http.HttpURLConnection");
//                if (cc.isFrozen()) {
//                    cc.defrost();
//                }
//                CtMethod personFly = cc.getDeclaredMethod("getInputStream");
////                if ("" instanceof HttpURLConnection.HttpInputStream)
//
//                personFly.insertAfter("System.out.println(\"--- after getInputStream ---\"); if($_ instanceof sun.net.www.protocol.http.HttpURLConnection$HttpInputStream ) {io.github.toohandsome.attach.util.InputStreamUtil.readInputStream($_); }");
//
//                return cc.toBytecode();
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
        if (className.equals("java/net/URL")) {
            try {
                //借助JavaAssist工具，进行字节码插桩

                ClassPool pool = ClassPool.getDefault();
                pool.importPackage("java.net");
//                pool.importPackage("sun.net.www.protocol.http");
                CtClass cc = pool.get("java.net.URL");
                if (cc.isFrozen()) {
                    cc.defrost();
                }
                CtMethod personFly = cc.getDeclaredMethod("openConnection");
//                if ("" instanceof HttpURLConnection.HttpInputStream)

                personFly.setBody("{ System.out.println(\"--- openConnection ---\"+this.getProtocol()); if(\"http\".equals(this.getProtocol())) {return this.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(\"127.0.0.1\",9999)));}else{return handler.openConnection(this);} }");

                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;


    }
}
