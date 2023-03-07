package io.github.toohandsome.attach;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
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

        if (className.equals("java/net/URL")) {
            try {
                ClassPool pool = ClassPool.getDefault();
                pool.importPackage("java.net");
                CtClass cc = pool.get("java.net.URL");
                if (cc.isFrozen()) {
                    cc.defrost();
                }
                CtMethod personFly = cc.getDeclaredMethod("openConnection");
                personFly.setBody("{ System.out.println(\"--- openConnection ---\"+this.getProtocol()); if(\"http\".equals(this.getProtocol())) {return this.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(\"127.0.0.1\",9999)));}else{return handler.openConnection(this);} }");
                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (className.equals("org/apache/http/client/config/RequestConfig")) {
            try {
                ClassPool pool = ClassPool.getDefault();
                pool.importPackage("org.apache.http");
                CtClass cc = pool.get("org.apache.http.client.config.RequestConfig");
                if (cc.isFrozen()) {
                    cc.defrost();
                }
                CtMethod personFly = cc.getDeclaredMethod("getProxy");
                personFly.setBody(" {return new HttpHost(\"127.0.0.1\",9999);}");
                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
//        else if (className.equals("okhttp3/Route")) {
//            try {
//                ClassPool pool = ClassPool.getDefault();
//                pool.importPackage("java.net");
//                pool.importPackage("okhttp3.Route");
//                CtClass cc = pool.get("okhttp3.Route");
//                if (cc.isFrozen()) {
//                    cc.defrost();
//                }
//                final CtConstructor constructor = cc.getConstructors()[0];
//                constructor.insertAfter(" this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(\"127.0.0.1\",9999)); ");
//
//                return cc.toBytecode();
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
        else if (className.equals("okhttp3/OkHttpClient")) {
            try {
                ClassPool pool = ClassPool.getDefault();
                pool.importPackage("java.net");
                CtClass cc = pool.get("okhttp3.OkHttpClient");
                if (cc.isFrozen()) {
                    cc.defrost();
                }
                CtMethod personFly = cc.getDeclaredMethod("proxy");
                personFly.setBody(" {  return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(\"127.0.0.1\",9999)); } ");
                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;


    }
}
