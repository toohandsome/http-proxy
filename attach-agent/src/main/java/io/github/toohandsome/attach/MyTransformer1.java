package io.github.toohandsome.attach;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
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
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        String replaceClassName = "";
        if (className != null) {
            replaceClassName = className.replace("/", ".");
            if (!AgentMain.retransformClassList.contains(replaceClassName)) {
                return null;
            }
        }
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = null;
        try {
            cc = pool.get(replaceClassName);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
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
                    declaredConstructor.insertBefore("$2 = io.github.toohandsome.attach.util.InputStreamUtil.cloneHttpConnectionInputStream($2,$1);");
                    declaredConstructor.insertAfter("if ($0 instanceof ChunkedInputStream){$0.in = io.github.toohandsome.attach.util.InputStreamUtil.cloneHttpConnectionInputStream1($0,$2,$1);}");
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
                ctMethod.insertBefore(" InputStreamUtil.getHttpConnectionRequestInfo($0,$1,$2);  ");
                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (className.equals("sun/net/www/protocol/http/HttpURLConnection")) {
            try {

                pool.importPackage("io.github.toohandsome.attach.util.InputStreamUtil");

                CtMethod ctMethod = cc.getDeclaredMethod("followRedirect");
                ctMethod.insertBefore("  InputStreamUtil.getHttpConnectionRedirectRespInfo($0);  ");
                return cc.toBytecode();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //
        } else if (className.equals("okhttp3/internal/http/CallServerInterceptor")) {
            try {


                pool.importPackage("java.util.Set");
                pool.importPackage("java.net.URL");
                pool.importPackage("java.util.List");
                pool.importPackage("java.util.ArrayList");
                pool.importPackage("java.io.ByteArrayOutputStream");
                pool.importPackage("okhttp3.RequestBody");
                pool.importPackage("okhttp3.Request");
                pool.importPackage("okhttp3.Call");
                pool.importPackage("okhttp3.HttpUrl");
                pool.importPackage("okhttp3.ResponseBody");
                pool.importPackage("okhttp3.Headers");
                pool.importPackage("okhttp3.internal.http.RealInterceptorChain");
                pool.importPackage("io.github.toohandsome.attach.entity.MyMap");
                pool.importPackage("io.github.toohandsome.attach.util.TrafficSendUtil");
                pool.importPackage("io.github.toohandsome.attach.entity.Traffic");
                pool.importPackage("io.github.toohandsome.attach.util.InputStreamUtil");
                pool.importPackage("okio.BufferedSource");
                pool.importPackage("okio.Buffer");
                pool.importPackage("okio.Okio");
                pool.importPackage("okio.GzipSource");
                pool.importPackage("okio.BufferedSink");
                pool.importPackage("java.nio.charset.Charset");
                pool.importPackage("java.nio.charset.StandardCharsets");
                pool.importPackage("java.util.zip.GZIPInputStream");
                pool.importPackage("java.io.InputStream");
                pool.importPackage("java.io.InputStreamReader");
                pool.importPackage("java.io.BufferedReader");
                pool.importPackage("java.lang.StringBuffer");
                pool.importPackage("java.io.ByteArrayInputStream");

                CtMethod ctMethod = cc.getDeclaredMethod("intercept");
                ctMethod.insertBefore("try{ \n" +
                        " Traffic traffic = new Traffic();\n" +
                        "   MyMap myMap = new MyMap(); \n" +
                        " RealInterceptorChain realChain_123 = (RealInterceptorChain) $1;\n" +
                        " Request request_123 = realChain_123.request(); \n" +
                        " traffic.setKey(request_123.hashCode() + \"\"); \n" +
                        "  traffic.setReqDate(System.currentTimeMillis()); \n" +
                        "   traffic.setDirection(\"up\"); \n" +
                        "  traffic.setRequestHeaders(myMap); \n" +
                        "   HttpUrl tempUrlObj =  request_123.url();  \n" +
                        "  traffic.setUrl(tempUrlObj.url().toString());  \n" +
                        "  traffic.setHost(tempUrlObj.host()); \n" +
                        "  Call call123 =  realChain_123.call().clone();\n" +

                        " Headers headers_123 = request_123.headers(); \n" +
                        " java.util.Collections.UnmodifiableSet headerSet_123 = headers_123.names(); \n" +
                        " List headerList_123 = new ArrayList(headerSet_123);\n " +
                        " for (int i = 0; i < headerList_123.size(); i++){  \n" +
                        " String tempKey_123 = (String) headerList_123.get(i); \n" +
                        "  System.out.println(\"req key: \"+ tempKey_123+\"  --  value: \" +headers_123.get(tempKey_123) ); \n" +
                        "        myMap.put(tempKey_123, headers_123.get(tempKey_123)); \n" +
                        "  }\n" +
                        "  RequestBody  body_123 = call123.request().body();\n" +
                        "  String reqBody2Str_123 =\"\";\n" +
                        "  if(body_123!=null){\n" +

                        "  ByteArrayOutputStream byteArrayOutputStream123 = new ByteArrayOutputStream(); \n" +
                        "  BufferedSink bufferedSink123 = Okio.buffer(Okio.sink(byteArrayOutputStream123));   \n" +
                        "        body_123.writeTo(bufferedSink123); \n" +
                        "        bufferedSink123.flush();  \n" +
                        "        bufferedSink123.close();  \n" +
                        "        byte[] bytes123 = byteArrayOutputStream123.toByteArray();\n" +
                        "        reqBody2Str_123 = new String(bytes123, StandardCharsets.UTF_8);\n" +
                        "        System.out.println(\"reqBody: \" + reqBody2Str_123);  \n" +
                        "  traffic.setReqBodyLength(body_123.contentLength() ); \n" +
                        "     }  \n" +
                        "  System.out.println(reqBody2Str_123); \n" +
                        "  traffic.setRequestBody(reqBody2Str_123 ); \n" +
                        "   TrafficSendUtil.send(traffic); \n" +
                        " } catch (Exception e123){ \n" +
                        "   e123.printStackTrace(); \n" +
                        "}");

                ctMethod.insertAfter("  Headers headers_123 = $_.headers();\n" +
                        " Traffic traffic = new Traffic();\n" +
                        "   MyMap myMap = new MyMap(); \n" +
                        "   String zipType = \"\"; \n" +
                        "  traffic.setRespDate(System.currentTimeMillis()); \n" +
                        "   traffic.setDirection(\"down\"); \n" +
                        "   traffic.setResponseHeaders(myMap); \n" +
                        "  traffic.setKey($1.request().hashCode() + \"\"); \n" +
                        "            java.util.Collections.UnmodifiableSet respNames_123 = headers_123.names();\n" +
                        "            List respHeaderList_123 = new ArrayList(respNames_123);\n " +
                        " for (int i = 0; i < respHeaderList_123.size(); i++){  \n" +
                        " String tempKey_123 = (String) respHeaderList_123.get(i); \n" +
                        "  if(\"Content-Encoding\".equalsIgnoreCase(tempKey_123)){ \n" +
                        " zipType = respHeaderList_123.get(i);   " +
                        "} \n" +
                        "                System.out.println(\"resp key: \" + tempKey_123 + \" -- value: \" + headers_123.get(tempKey_123));\n" +
                        "        myMap.put(tempKey_123, headers_123.get(tempKey_123)); \n" +
                        "            }\n" +
                        "   ResponseBody respBody_123 = $_.body();\n" +
                        "    String respBody2Str_123 = \"\";\n" +
                        "            BufferedSource  source_123 = respBody_123.source();\n" +
                        "           Buffer buffer_123 = source_123.buffer();  \n" +
                        "     traffic.setRespBodyLength(buffer_123.size()); \n" +
                        " if(\"gzip\".equalsIgnoreCase(zipType)){ \n" +

                        "  ByteArrayOutputStream byteArrayOutputStream123 = new ByteArrayOutputStream(); \n" +
                        "   byte[] orgBytes = respBody_123.bytes(); \n"+
                        "       byteArrayOutputStream123.write(orgBytes, 0, orgBytes.length); \n" +
                        "       orgBytes =  byteArrayOutputStream123.toByteArray(); \n" +
                        "        byte[] bytes123 = byteArrayOutputStream123.toByteArray();\n" +
                        "      InputStream input123 = new GZIPInputStream(new ByteArrayInputStream(bytes123));\n" +
                        "                BufferedReader reader123 = new BufferedReader(new InputStreamReader(input123, \"utf-8\"));\n" +
                        "                StringBuffer respBodyBuffer = new StringBuffer();\n" +
                        "                String line123 = \"\";\n" +
                        "                while ((line123 = reader123.readLine()) != null) {\n" +
                        "                    respBodyBuffer.append(line123+\"\\r\\n\");\n" +
                        "                }\n" +
                        "                respBody2Str_123 = respBodyBuffer.toString(); \n" +
                        "       \n" +
                        "    } else{   \n" +

                        "            source_123.request(Long.MAX_VALUE);  \n" +


                        "    \n" +
                        "           respBody2Str_123 = buffer_123.clone().readString(Charset.forName(\"UTF-8\"));  \n" +
                        " } \n" +
                        "       traffic.setResponseBody(respBody2Str_123);\n" +
                        "            System.out.println(\"respBody: \"+respBody2Str_123); \n" +
                        "   TrafficSendUtil.send(traffic); \n" +

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
                pool.importPackage("io.github.toohandsome.attach.entity.MyMap");
                pool.importPackage("io.github.toohandsome.attach.util.TrafficSendUtil");
                pool.importPackage("io.github.toohandsome.attach.entity.Traffic");
                pool.importPackage("io.github.toohandsome.attach.util.InputStreamUtil");
                pool.importPackage("org.apache.http.client.methods.HttpRequestWrapper");

                CtMethod ctMethod = cc.getDeclaredMethod("execute");
                ctMethod.insertBefore(" Traffic traffic = new Traffic();\n" +
                        "   MyMap myMap = new MyMap(); \n" +
                        "    myMap.put($1.getRequestLine().toString(), \"null\");  \n" +
                        " //traffic.setUrl(client.getURLFile()); \n" +
                        " traffic.setKey($1.hashCode() + \"\"); \n" +
                        "  traffic.setReqDate(System.currentTimeMillis()); \n" +
                        "   traffic.setDirection(\"up\"); \n" +
                        "  traffic.setRequestHeaders(myMap); \n" +
                        " HttpRequestWrapper request1 = (HttpRequestWrapper) $1; \n" +
                        "  traffic.setUrl(request1.getURI().toString());  \n" +
                        "  traffic.setHost(request1.getTarget().toString()   ); \n" +
                        "    \n" +
                        "   \n" +
                        "   Header[]  tempHttpMessageArr =  $1.getAllHeaders();  \n" +
                        " for (int i = 0; i < tempHttpMessageArr.length; i++){  \n" +
                        " Header tempHeader =  tempHttpMessageArr[i]; \n" +
                        "               System.out.println(\"req key:  \" + tempHeader.getName() + \" -- value: \" + tempHeader.getValue());\n" +
                        "        myMap.put(tempHeader.getName(), tempHeader.getValue()); \n" +
                        "            }\n" +
                        "   \n" +

                        "  try{ " +
                        "   Field tempEntity =  $1.getClass().getDeclaredField(\"entity\");    \n" +
                        "    tempEntity.setAccessible(true);   \n" +
                        "   HttpEntity tempEntityObj = (HttpEntity)  tempEntity.get(request);  \n" +
                        "  if(tempEntityObj!=null){  \n" +
                        "   InputStream tempStream =   tempEntityObj.getContent()  ;   \n" +
                        "  if(tempStream!=null){   \n" +
                        "   tempStream =  InputStreamUtil.cloneHttpClientInputStream(tempStream,traffic ) ;  \n" +
                        "    } \n" +
                        " }\n" +
                        "   TrafficSendUtil.send(traffic); \n" +
                        "   }catch (Exception e1) {\n" +
                        "            e1.printStackTrace();\n" +
                        "        }  \n" +
                        "       \n" +
                        "       \n" +
                        "");
                ctMethod.insertAfter(" Traffic traffic = new Traffic();\n" +
                        "   MyMap myMap = new MyMap(); \n" +
                        "  traffic.setResponseHeaders(myMap); \n" +
                        "  traffic.setRespDate(System.currentTimeMillis());\n" +
                        "   traffic.setDirection(\"down\"); \n" +
                        "    myMap.put( \"null\",$_.getStatusLine().toString());  \n" +
                        "   traffic.setKey($1.hashCode() + \"\"); \n" +
                        "   Header[]  tempHttpMessageArr1 =  $_.getAllHeaders();  \n" +
                        "   String zipType = \"\"; \n" +
                        " for (int i = 0; i < tempHttpMessageArr1.length; i++){  \n" +
                        " Header tempHeader =  tempHttpMessageArr1[i]; \n" +
                        "  if(\"Content-Encoding\".equalsIgnoreCase(tempHeader.getName())){ \n" +
                        " zipType = tempHeader.getValue();   " +
                        "} \n" +
                        "               System.out.println(\"resp key:  \" + tempHeader.getName() + \" -- value: \" + tempHeader.getValue());\n" +
                        "        myMap.put(tempHeader.getName(), tempHeader.getValue()); \n" +
                        "            }\n" +
                        "   \n" +

                        "  try{ " +

                        "   HttpEntity tempEntityObj1 =  $_.getEntity();  \n" +
                        "  $_.setEntity( new BufferedHttpEntity(tempEntityObj1));  \n" +
                        "  if(tempEntityObj1 != null){  " +

                        "  HttpEntity tempEntityObj2 = $_.getEntity();   \n" +
                        "  InputStream tmpInputStream1 =   tempEntityObj2.getContent();   \n" +
                        "  tmpInputStream1=   InputStreamUtil.cloneHttpClientInputStream1(tmpInputStream1,traffic,zipType);   \n" +
                        "   TrafficSendUtil.send(traffic);    \n" +

                        "       \n" +

//                        "    System.out.println(\"respBody: \"+EntityUtils.toString( $_.getEntity()));   \n" +
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
