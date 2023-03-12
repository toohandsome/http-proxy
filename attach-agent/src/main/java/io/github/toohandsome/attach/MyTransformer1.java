package io.github.toohandsome.attach;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.ProtectionDomain;

/**
 * @author Administrator
 */
public class MyTransformer1 implements ClassFileTransformer {

    private String port;

    public MyTransformer1(String port) {
        this.port = port;
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

//        if (className.equals("java/net/URL")) {
//            try {
//                ClassPool pool = ClassPool.getDefault();
//                pool.importPackage("java.net");
//                CtClass cc = pool.get("java.net.URL");
//                if (cc.isFrozen()) {
//                    cc.defrost();
//                }
//                CtMethod personFly = cc.getDeclaredMethod("openConnection");
//                personFly.setBody("{ System.out.println(\"--- openConnection ---\"+this.getProtocol()); if(\"http\".equals(this.getProtocol())) {return this.openConnection(io.github.toohandsome.attach.util.ProxyIns.PROXY);}else{return handler.openConnection(this);} }");
//                return cc.toBytecode();
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        } else
        if (className.equals("sun/net/www/protocol/http/HttpURLConnection$HttpInputStream")) {
            try {
                ClassPool pool = ClassPool.getDefault();
                pool.importPackage("io.github.toohandsome.attach.util.InputStreamUtil");
                CtClass cc = pool.get("sun.net.www.protocol.http.HttpURLConnection$HttpInputStream");
                if (cc.isFrozen()) {
                    cc.defrost();
                }
                CtConstructor[] declaredConstructors = cc.getDeclaredConstructors();
                for (CtConstructor declaredConstructor : declaredConstructors) {
                    declaredConstructor.insertBefore("$2 = io.github.toohandsome.attach.util.InputStreamUtil.cloneInputStream($2);");
                    declaredConstructor.insertAfter("$0.in = io.github.toohandsome.attach.util.InputStreamUtil.cloneInputStream($0,$2);");
                }

                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (className.equals("okhttp3/internal/http/CallServerInterceptor")) {
            try {
                ClassPool pool = ClassPool.getDefault();
                pool.importPackage("okhttp3.Headers");
                pool.importPackage("java.util.Set");
                pool.importPackage("java.util.List");
                pool.importPackage("java.util.ArrayList");
                pool.importPackage("okhttp3.ResponseBody");
                pool.importPackage("okhttp3.Request");
                pool.importPackage("okhttp3.internal.http.RealInterceptorChain");
//                pool.importPackage("java.lang.reflect.Method");
                CtClass cc = pool.get("okhttp3.internal.http.CallServerInterceptor");
                if (cc.isFrozen()) {
                    cc.defrost();
                }

                CtMethod personFly = cc.getDeclaredMethod("intercept");

//                personFly.insertBefore(" Class personClass = RealInterceptorChain.class; \n" +
//                        " Method[] method1s = personClass.getDeclaredMethods(); \n  " +
//                        "  for (int i = 0; i < method1s.length; i++) {\n" +
//                        "                    Method ctConstructor = method1s[i];\n" +
//                        "                    System.out.println(ctConstructor);\n" +
//                        "                }");
                personFly.insertBefore(" RealInterceptorChain realChain_uid_123_abc_uid = (RealInterceptorChain) $1;\n" +
                        " Request request_uid_123_abc_uid = realChain_uid_123_abc_uid.request(); \n"
                        +
                        " Headers headers1_uid_123_abc_uid = request_uid_123_abc_uid.headers(); \n"
                +
                        " java.util.Collections.UnmodifiableSet headerSet_uid_123 = headers1_uid_123_abc_uid.names(); \n" +
                        " List headerList_uid_123 = new ArrayList(headerSet_uid_123);\n "+
                        " for (int i = 0; i < headerList_uid_123.size(); i++){  \n" +
                        " String tempKey_uid_123 = (String) headerList_uid_123.get(i); \n"+
                        "  System.out.println(\"req key: \"+ tempKey_uid_123+\"  --  value: \" +headers1_uid_123_abc_uid.get(tempKey_uid_123) ); \n" +
                        "  }\n" +
                        " System.out.println(request_uid_123_abc_uid.body()); \n" +
                        "");
                personFly.insertAfter("  Headers headers_uid_abc_123_uid = $_.headers();\n" +
                        "            java.util.Collections.UnmodifiableSet respNames_uid_abc_123_uid = headers_uid_abc_123_uid.names();\n" +
                        "            List respHeaderList_uid_123 = new ArrayList(respNames_uid_abc_123_uid);\n "+
                        " for (int i = 0; i < respHeaderList_uid_123.size(); i++){  \n" +
                        " String tempKey_uid_abc_123_uid = (String) respHeaderList_uid_123.get(i); \n"+
                        "                System.out.println(\"resp key: \" + tempKey_uid_abc_123_uid + \" -- value: \" + headers_uid_abc_123_uid.get(tempKey_uid_abc_123_uid));\n" +
                        "            }\n" +
                        "   ResponseBody respBody_uid_abc_123_uid = $_.body();\n" +
                        "            String respBody2Str_uid_abc_123_uid = respBody_uid_abc_123_uid.string();\n" +
                        "            System.out.println(\"respBody: \"+respBody2Str_uid_abc_123_uid); \n" +

                        "");


                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
//            else if (className.equals("org/apache/http/client/config/RequestConfig")) {
//            try {
//                ClassPool pool = ClassPool.getDefault();
//                pool.importPackage("org.apache.http");
//                CtClass cc = pool.get("org.apache.http.client.config.RequestConfig");
//                if (cc.isFrozen()) {
//                    cc.defrost();
//                }
//                CtMethod personFly = cc.getDeclaredMethod("getProxy");
//                personFly.setBody(" {return new HttpHost(\"127.0.0.1\"," + port + ");}");
//                return cc.toBytecode();
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        } else if (className.equals("cn/hutool/http/HttpConnection")) {
//            try {
//                ClassPool pool = ClassPool.getDefault();
//                pool.importPackage("java.net");
//                CtClass cc = pool.get("cn.hutool.http.HttpConnection");
//                if (cc.isFrozen()) {
//                    cc.defrost();
//                }
//                CtMethod personFly = cc.getDeclaredMethod("openConnection");
//                personFly.setBody("{ return  url.openConnection(io.github.toohandsome.attach.util.ProxyIns.PROXY); }");
//                return cc.toBytecode();
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        } else if (className.equals("okhttp3/OkHttpClient")) {
//            try {
//                ClassPool pool = ClassPool.getDefault();
//                pool.importPackage("java.net");
//                CtClass cc = pool.get("okhttp3.OkHttpClient");
//                if (cc.isFrozen()) {
//                    cc.defrost();
//                }
//                CtMethod personFly = cc.getDeclaredMethod("proxy");
//                personFly.setBody(" {  return io.github.toohandsome.attach.util.ProxyIns.PROXY; } ");
//                return cc.toBytecode();
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
        return null;

    }
}
