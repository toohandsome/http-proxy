package io.github.toohandsome.attach;

import io.github.toohandsome.attach.entity.AgentInfo;
import io.github.toohandsome.attach.patch.inner.HttpClientInnerPatch;
import io.github.toohandsome.attach.patch.inner.HttpURLConnectionInnerPatch;
import io.github.toohandsome.attach.patch.inner.OkhttpInnerPatch;
import io.github.toohandsome.attach.util.AgentInfoSendUtil;
import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * @author Administrator
 */
public class ReWriteHttpTransformer implements ClassFileTransformer {

    private String port;
    ClassPool pool = ClassPool.getDefault();

    public ReWriteHttpTransformer(String port) {
        this.port = port;
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
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
            if (className.equals("sun/net/www/protocol/http/HttpURLConnection$HttpInputStream")) {

                CtConstructor[] declaredConstructors = cc.getDeclaredConstructors();
                HttpURLConnectionInnerPatch httpURLConnectionInnerPatch = HttpURLConnectionInnerPatch.getInstance(pool);
                for (CtConstructor declaredConstructor : declaredConstructors) {
                    declaredConstructor.insertBefore(httpURLConnectionInnerPatch.HttpURLConnection$HttpInputStream_Constructor_Before());
                    declaredConstructor.insertAfter(httpURLConnectionInnerPatch.HttpURLConnection$HttpInputStream_Constructor_After());
                }

            } else if (className.equals("sun/net/www/http/HttpClient")) {
                HttpURLConnectionInnerPatch httpURLConnectionInnerPatch = HttpURLConnectionInnerPatch.getInstance(pool);
                CtClass cc1 = pool.get("sun.net.www.MessageHeader");
                CtClass cc2 = pool.get("sun.net.www.http.PosterOutputStream");
                CtClass[] ctClasses = new CtClass[2];
                ctClasses[0] = cc1;
                ctClasses[1] = cc2;
                CtMethod ctMethod = cc.getDeclaredMethod("writeRequests", ctClasses);
                ctMethod.insertBefore(httpURLConnectionInnerPatch.HttpClient_writeRequests_Before());
            } else if (className.equals("sun/net/www/protocol/http/HttpURLConnection")) {

                HttpURLConnectionInnerPatch httpURLConnectionInnerPatch = HttpURLConnectionInnerPatch.getInstance(pool);
                CtMethod ctMethod = cc.getDeclaredMethod("followRedirect");
                ctMethod.insertBefore(httpURLConnectionInnerPatch.HttpURLConnectiont_followRedirect_Before());
            } else if (className.equals("okhttp3/internal/http/CallServerInterceptor")) {

                CtMethod ctMethod = cc.getDeclaredMethod("intercept");
                OkhttpInnerPatch okhttpInnerPatch = new OkhttpInnerPatch(pool);
                ctMethod.insertBefore(okhttpInnerPatch.CallServerInterceptor_intercept_Before());
                ctMethod.insertAfter(okhttpInnerPatch.CallServerInterceptor_intercept_After());
            } else if (className.equals("org/apache/http/protocol/HttpRequestExecutor")) {

                HttpClientInnerPatch httpClientInnerPatch = new HttpClientInnerPatch(pool);
                CtMethod ctMethod = cc.getDeclaredMethod("execute");
                ctMethod.insertBefore(httpClientInnerPatch.HttpRequestExecutor_execute_Before());
                ctMethod.insertAfter(httpClientInnerPatch.HttpRequestExecutor_execute_After());
            }

            // proxy
            if (className.equals("java/net/URL")) {

                pool.importPackage("java.net");

                CtMethod ctMethod = cc.getDeclaredMethod("openConnection");
                ctMethod.setBody("{ System.out.println(\"--- openConnection ---\"+this.getProtocol()); if(\"http\".equals(this.getProtocol()) || \"https\".equals(this.getProtocol())) {return this.openConnection(io.github.toohandsome.attach.util.ProxyIns.PROXY);}else{return handler.openConnection(this);} }");
            } else if (className.equals("org/apache/http/client/config/RequestConfig")) {

                pool.importPackage("org.apache.http");

                CtMethod ctMethod = cc.getDeclaredMethod("getProxy");
                ctMethod.setBody(" {return new HttpHost(\"127.0.0.1\"," + port + ");}");
            } else if (className.equals("cn/hutool/http/HttpConnection")) {

                pool.importPackage("java.net");

                CtMethod ctMethod = cc.getDeclaredMethod("openConnection");
                ctMethod.setBody("{ return  url.openConnection(io.github.toohandsome.attach.util.ProxyIns.PROXY); }");
            } else if (className.equals("okhttp3/OkHttpClient")) {

                pool.importPackage("java.net");

                CtMethod ctMethod = cc.getDeclaredMethod("proxy");
                ctMethod.setBody(" {  return io.github.toohandsome.attach.util.ProxyIns.PROXY; } ");
            }
            AgentInfoSendUtil.send(new AgentInfo().setType("info").setMsg("calss: " + className + " transform success .."));
            return cc.toBytecode();
        } catch (Exception ex) {
            ex.printStackTrace();
            AgentInfoSendUtil.sendExcepTion(ex);
        }
        return null;
    }
}
