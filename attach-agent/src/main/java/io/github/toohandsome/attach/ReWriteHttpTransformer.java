package io.github.toohandsome.attach;

import io.github.toohandsome.attach.config.GlobalConfig;
import io.github.toohandsome.attach.entity.AgentInfo;
import io.github.toohandsome.attach.patch.inner.*;
import io.github.toohandsome.attach.util.AgentInfoSendUtil;
import io.github.toohandsome.attach.core.Reset;
import io.github.toohandsome.attach.core.WhiteListCache;
import javassist.*;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.ProtectionDomain;

/**
 * @author Administrator
 */
public class ReWriteHttpTransformer implements ClassFileTransformer {


    ClassPool pool = ClassPool.getDefault();

    public ReWriteHttpTransformer(String args) {
        String[] split = args.split(";");
        GlobalConfig.listenPort = split[0];
        String whiteListPath = split[1];
        GlobalConfig.printStack = Boolean.valueOf(split[2]);
        GlobalConfig.proxyMode = Boolean.valueOf(split[3]);
        GlobalConfig.proxyPort = split[4];

        File file = new File(whiteListPath);
        if (file.exists()) {
            try {
                WhiteListCache.whiteList = Files.readAllLines(Paths.get(whiteListPath));
            } catch (IOException e) {
                AgentInfoSendUtil.sendExcepTion(e);
            }
        }
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classFileBuffer) {
        try {
            String replaceClassName = "";
            if (className != null) {
                replaceClassName = className.replace("/", ".");
                if (!AgentMain.retransformClassList.contains(replaceClassName)) {
                    return null;
                }
            } else {
                return null;
            }
            CtClass cc = null;
            try {
                cc = pool.get(replaceClassName);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("className: " + className);
                return null;
            }
            if (cc.isFrozen()) {
                cc.defrost();
            }
            if ("sun/net/www/protocol/http/HttpURLConnection$HttpInputStream".equals(className)) {

                CtConstructor[] declaredConstructors = cc.getDeclaredConstructors();
                HttpURLConnectionInnerPatch httpURLConnectionInnerPatch = HttpURLConnectionInnerPatch.getInstance(pool);
                for (CtConstructor declaredConstructor : declaredConstructors) {
                    declaredConstructor.insertBefore(httpURLConnectionInnerPatch.HttpURLConnection$HttpInputStream_Constructor_Before());
                    declaredConstructor.insertAfter(httpURLConnectionInnerPatch.HttpURLConnection$HttpInputStream_Constructor_After());
                }

            } else if ("sun/net/www/http/HttpClient".equals(className)) {
                HttpURLConnectionInnerPatch httpURLConnectionInnerPatch = HttpURLConnectionInnerPatch.getInstance(pool);
                CtClass cc1 = pool.get("sun.net.www.MessageHeader");
                CtClass cc2 = pool.get("sun.net.www.http.PosterOutputStream");
                CtClass[] ctClasses = new CtClass[2];
                ctClasses[0] = cc1;
                ctClasses[1] = cc2;
                CtMethod ctMethod = cc.getDeclaredMethod("writeRequests", ctClasses);
                ctMethod.insertBefore(httpURLConnectionInnerPatch.HttpClient_writeRequests_Before());
            } else if ("sun/net/www/protocol/http/HttpURLConnection".equals(className)) {

                HttpURLConnectionInnerPatch httpURLConnectionInnerPatch = HttpURLConnectionInnerPatch.getInstance(pool);
                CtMethod ctMethod = cc.getDeclaredMethod("followRedirect");
                ctMethod.insertBefore(httpURLConnectionInnerPatch.HttpURLConnectiont_followRedirect_Before());
            } else if ("okhttp3/internal/http/CallServerInterceptor".equals(className)) {

                CtMethod ctMethod = cc.getDeclaredMethod("intercept");
                OkhttpInnerPatch okhttpInnerPatch = new OkhttpInnerPatch(pool);
                ctMethod.insertBefore(okhttpInnerPatch.CallServerInterceptor_intercept_Before());
                ctMethod.insertAfter(okhttpInnerPatch.CallServerInterceptor_intercept_After());
            } else if ("org/apache/http/protocol/HttpRequestExecutor".equals(className)) {

                HttpClientInnerPatch httpClientInnerPatch = new HttpClientInnerPatch(pool);
                CtMethod ctMethod = cc.getDeclaredMethod("execute");
                ctMethod.insertBefore(httpClientInnerPatch.HttpRequestExecutor_execute_Before());
                ctMethod.insertAfter(httpClientInnerPatch.HttpRequestExecutor_execute_After());
            } else if ("tomcat".equals(className)) {

                TomcatInnerPatch httpClientInnerPatch = new TomcatInnerPatch(pool);
                CtMethod ctMethod = cc.getDeclaredMethod("execute");
//                ctMethod.insertBefore(httpClientInnerPatch.HttpRequestExecutor_execute_Before());
//                ctMethod.insertAfter(httpClientInnerPatch.HttpRequestExecutor_execute_After());
            } else if ("jetty".equals(className)) {

                JettyInnerPatch httpClientInnerPatch = new JettyInnerPatch(pool);
                CtMethod ctMethod = cc.getDeclaredMethod("execute");
//                ctMethod.insertBefore(httpClientInnerPatch.HttpRequestExecutor_execute_Before());
//                ctMethod.insertAfter(httpClientInnerPatch.HttpRequestExecutor_execute_After());
            } else if ("undertow".equals(className)) {

                UndertowInnerPatch httpClientInnerPatch = new UndertowInnerPatch(pool);
                CtMethod ctMethod = cc.getDeclaredMethod("execute");
//                ctMethod.insertBefore(httpClientInnerPatch.HttpRequestExecutor_execute_Before());
//                ctMethod.insertAfter(httpClientInnerPatch.HttpRequestExecutor_execute_After());
            }

            // proxy
            if ("java/net/URL".equals(className)) {

                pool.importPackage("java.net");

                CtMethod ctMethod = cc.getDeclaredMethod("openConnection");
                ctMethod.setBody("{ System.out.println(\"--- openConnection ---\"+this.getProtocol()); if(\"http\".equals(this.getProtocol()) || \"https\".equals(this.getProtocol())) {return this.openConnection(io.github.toohandsome.attach.util.ProxyIns.PROXY);}else{return handler.openConnection(this);} }");
            } else if ("org/apache/http/client/config/RequestConfig".equals(className)) {

                pool.importPackage("org.apache.http");

                CtMethod ctMethod = cc.getDeclaredMethod("getProxy");
                ctMethod.setBody(" {return new HttpHost(\"127.0.0.1\"," + GlobalConfig.listenPort + ");}");
            } else if ("cn/hutool/http/HttpConnection".equals(className)) {

                pool.importPackage("java.net");

                CtMethod ctMethod = cc.getDeclaredMethod("openConnection");
                ctMethod.setBody("{ return  url.openConnection(io.github.toohandsome.attach.util.ProxyIns.getProxy()); }");
            } else if ("okhttp3/OkHttpClient".equals(className)) {

                pool.importPackage("java.net");

                CtMethod ctMethod = cc.getDeclaredMethod("proxy");
                ctMethod.setBody(" {  return io.github.toohandsome.attach.util.ProxyIns.getProxy(); } ");
            }
            AgentInfoSendUtil.send(new AgentInfo().setType("info").setMsg("class: " + className + " transform success .."));

            Reset.classMap.put(replaceClassName, classBeingRedefined);
            return cc.toBytecode();
        } catch (Exception ex) {
            ex.printStackTrace();
            AgentInfoSendUtil.sendExcepTion(ex);
        }
        return null;
    }
}
