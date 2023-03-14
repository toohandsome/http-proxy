package io.github.toohandsome.attach;

import javassist.*;
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
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = null;
        try {
            cc = pool.get(className.replace("/", "."));
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        if (cc.isFrozen()) {
            cc.defrost();
        }
        if (className.equals("sun/net/www/protocol/http/HttpURLConnection$HttpInputStream")) {
            try {

                pool.importPackage("io.github.toohandsome.attach.util.InputStreamUtil");
                pool.importPackage("sun.net.www.http.ChunkedInputStream");

                CtConstructor[] declaredConstructors = cc.getDeclaredConstructors();
                for (CtConstructor declaredConstructor : declaredConstructors) {
                    declaredConstructor.insertBefore("$2 = io.github.toohandsome.attach.util.InputStreamUtil.cloneInputStream($2,$1);");
                    declaredConstructor.insertAfter("if ($0 instanceof ChunkedInputStream){$0.in = io.github.toohandsome.attach.util.InputStreamUtil.cloneInputStream($0,$2,$1);}");
                }

                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (className.equals("sun/net/www/http/HttpClient")) {
            try {

                pool.importPackage("io.github.toohandsome.attach.util.InputStreamUtil");

                CtClass cc1 = pool.get("sun.net.www.MessageHeader");
                CtClass cc2 = pool.get("sun.net.www.http.PosterOutputStream");
                CtClass[] ctClasses = new CtClass[2];
                ctClasses[0] = cc1;
                ctClasses[1] = cc2;
                CtMethod ctMethod = cc.getDeclaredMethod("writeRequests", ctClasses);
                ctMethod.insertBefore(" InputStreamUtil.getRequestInfo($0,$1,$2);  ");
                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (className.equals("okhttp3/internal/http/CallServerInterceptor")) {
            try {

                pool.importPackage("okhttp3.Headers");
                pool.importPackage("java.util.Set");
                pool.importPackage("java.util.List");
                pool.importPackage("java.util.ArrayList");
                pool.importPackage("okhttp3.ResponseBody");
                pool.importPackage("okhttp3.Request");
                pool.importPackage("okhttp3.internal.http.RealInterceptorChain");


                CtMethod ctMethod = cc.getDeclaredMethod("intercept");

                ctMethod.insertBefore(" RealInterceptorChain realChain_uid_123_abc_uid = (RealInterceptorChain) $1;\n" +
                        " Request request_uid_123_abc_uid = realChain_uid_123_abc_uid.request(); \n"
                        +
                        " Headers headers1_uid_123_abc_uid = request_uid_123_abc_uid.headers(); \n"
                        +
                        " java.util.Collections.UnmodifiableSet headerSet_uid_123 = headers1_uid_123_abc_uid.names(); \n" +
                        " List headerList_uid_123 = new ArrayList(headerSet_uid_123);\n " +
                        " for (int i = 0; i < headerList_uid_123.size(); i++){  \n" +
                        " String tempKey_uid_123 = (String) headerList_uid_123.get(i); \n" +
                        "  System.out.println(\"req key: \"+ tempKey_uid_123+\"  --  value: \" +headers1_uid_123_abc_uid.get(tempKey_uid_123) ); \n" +
                        "  }\n" +
                        " System.out.println(request_uid_123_abc_uid.body()); \n" +
                        "");
                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (className.equals("okhttp3/internal/http/BridgeInterceptor")) {
            try {

                pool.importPackage("okhttp3.Headers");
                pool.importPackage("java.util.Set");
                pool.importPackage("java.util.List");
                pool.importPackage("java.util.ArrayList");
                pool.importPackage("okhttp3.ResponseBody");
                pool.importPackage("okhttp3.Request");
                pool.importPackage("okhttp3.internal.http.RealInterceptorChain");
                pool.importPackage("okio.BufferedSource");
                pool.importPackage("okio.Buffer");
                pool.importPackage("java.nio.charset.Charset");

                CtMethod ctMethod = cc.getDeclaredMethod("intercept");
                ctMethod.insertAfter("  Headers headers_uid_abc_123_uid = $_.headers();\n" +
                        "            java.util.Collections.UnmodifiableSet respNames_uid_abc_123_uid = headers_uid_abc_123_uid.names();\n" +
                        "            List respHeaderList_uid_123 = new ArrayList(respNames_uid_abc_123_uid);\n " +
                        " for (int i = 0; i < respHeaderList_uid_123.size(); i++){  \n" +
                        " String tempKey_uid_abc_123_uid = (String) respHeaderList_uid_123.get(i); \n" +
                        "                System.out.println(\"resp key: \" + tempKey_uid_abc_123_uid + \" -- value: \" + headers_uid_abc_123_uid.get(tempKey_uid_abc_123_uid));\n" +
                        "            }\n" +
                        "   ResponseBody respBody_uid_abc_123_uid = $_.body();\n" +
                        "            BufferedSource  source_uid_abc_123_uid = respBody_uid_abc_123_uid.source();\n" +
                        "            source_uid_abc_123_uid.request(Long.MAX_VALUE);  \n" +
                        "           Buffer buffer_uid_abc_123_uid = source_uid_abc_123_uid.buffer();  \n" +
                        "           String respBody2Str_uid_abc_123_uid = buffer_uid_abc_123_uid.clone().readString(Charset.forName(\"UTF-8\"));  \n" +
                        "            System.out.println(\"respBody: \"+respBody2Str_uid_abc_123_uid); \n" +

                        "");

                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (className.equals("org/apache/http/protocol/HttpRequestExecutor")) {
            try {

                pool.importPackage("java.lang.reflect.Field");
                pool.importPackage("org.apache.http.HttpEntity");
                pool.importPackage("java.io.InputStream");
                pool.importPackage("org.apache.http.Header");
                pool.importPackage("org.apache.http.util.EntityUtils");
                pool.importPackage("org.apache.http.entity.BufferedHttpEntity");

                CtMethod ctMethod = cc.getDeclaredMethod("execute");
                ctMethod.insertBefore(" Header[]  tempHttpMessageArr =  $1.getAllHeaders();  \n" +
                        " for (int i = 0; i < tempHttpMessageArr.length; i++){  \n" +
                        " Header tempHeader =  tempHttpMessageArr[i]; \n" +
                        "               System.out.println(\"req key:  \" + tempHeader.getName() + \" -- value: \" + tempHeader.getValue());\n" +
                        "            }\n" +
                        "   \n" +

                        "  try{ " +
                        "   Field tempEntity =  $1.getClass().getDeclaredField(\"entity\");    \n" +
                        "    tempEntity.setAccessible(true);   \n" +
                        "   HttpEntity tempEntityObj = (HttpEntity)  tempEntity.get(request);  \n" +
                        "  if(tempEntityObj!=null){  " +
                        "   InputStream tempStream =   tempEntityObj.getContent()  ;   \n" +
                        "  if(tempStream!=null){   " +
                        "   tempStream =  io.github.toohandsome.attach.util.InputStreamUtil.cloneInputStream(tempStream,null, null ) ;  \n" +
                        "    }" +
                        " }" +
                        "   }catch (Exception e1) {\n" +
                        "            e1.printStackTrace();\n" +
                        "        }  \n" +
                        "       \n" +
                        "       \n" +
                        "");
                ctMethod.insertAfter(" Header[]  tempHttpMessageArr1 =  $_.getAllHeaders();  \n" +
                        " for (int i = 0; i < tempHttpMessageArr1.length; i++){  \n" +
                        " Header tempHeader =  tempHttpMessageArr1[i]; \n" +
                        "               System.out.println(\"resp key:  \" + tempHeader.getName() + \" -- value: \" + tempHeader.getValue());\n" +
                        "            }\n" +
                        "   \n" +

                        "  try{ " +

                        "   HttpEntity tempEntityObj1 =  $_.getEntity();  \n" +
                        "  if(tempEntityObj1 != null){  " +
                        "  $_.setEntity( new BufferedHttpEntity(tempEntityObj1));  \n" +
                        "    System.out.println(\"respBody: \"+EntityUtils.toString( $_.getEntity()));   \n" +
                        "       \n" +
                        " }" +
                        "   }catch (Exception e2) {\n" +
                        "            e2.printStackTrace();\n" +
                        "        }  \n" +
                        "       \n" +
                        "       \n" +
                        "");
                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        // proxy
        if (className.equals("java/net/URL")) {
            try {

                pool.importPackage("java.net");

                CtMethod ctMethod = cc.getDeclaredMethod("openConnection");
                ctMethod.setBody("{ System.out.println(\"--- openConnection ---\"+this.getProtocol()); if(\"http\".equals(this.getProtocol()) || \"https\".equals(this.getProtocol())) {return this.openConnection(io.github.toohandsome.attach.util.ProxyIns.PROXY);}else{return handler.openConnection(this);} }");
                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (className.equals("org/apache/http/client/config/RequestConfig")) {
            try {

                pool.importPackage("org.apache.http");

                CtMethod ctMethod = cc.getDeclaredMethod("getProxy");
                ctMethod.setBody(" {return new HttpHost(\"127.0.0.1\"," + port + ");}");
                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (className.equals("cn/hutool/http/HttpConnection")) {
            try {

                pool.importPackage("java.net");

                CtMethod ctMethod = cc.getDeclaredMethod("openConnection");
                ctMethod.setBody("{ return  url.openConnection(io.github.toohandsome.attach.util.ProxyIns.PROXY); }");
                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (className.equals("okhttp3/OkHttpClient")) {
            try {

                pool.importPackage("java.net");

                CtMethod ctMethod = cc.getDeclaredMethod("proxy");
                ctMethod.setBody(" {  return io.github.toohandsome.attach.util.ProxyIns.PROXY; } ");
                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;

    }
}
