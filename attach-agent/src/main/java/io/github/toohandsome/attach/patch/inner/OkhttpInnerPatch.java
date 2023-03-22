//package io.github.toohandsome.attach.patch.inner;
//
//import javassist.ClassPool;
//
//public class OkhttpInnerPatch {
//    public OkhttpInnerPatch(ClassPool pool) {
//        pool.importPackage("java.util.Set");
//        pool.importPackage("java.net.URL");
//        pool.importPackage("java.util.List");
//        pool.importPackage("java.util.ArrayList");
//        pool.importPackage("java.io.ByteArrayOutputStream");
//        pool.importPackage("okhttp3.RequestBody");
//        pool.importPackage("okhttp3.Request");
//        pool.importPackage("okhttp3.Call");
//        pool.importPackage("okhttp3.HttpUrl");
//        pool.importPackage("okhttp3.ResponseBody");
//        pool.importPackage("okhttp3.Headers");
//        pool.importPackage("okhttp3.internal.http.RealInterceptorChain");
//        pool.importPackage("io.github.toohandsome.attach.entity.MyMap");
//        pool.importPackage("io.github.toohandsome.attach.util.AgentInfoSendUtil");
//        pool.importPackage("io.github.toohandsome.attach.entity.Traffic");
//        pool.importPackage("io.github.toohandsome.attach.util.InputStreamUtil");
//        pool.importPackage("okio.BufferedSource");
//        pool.importPackage("okio.Buffer");
//        pool.importPackage("okio.Okio");
//        pool.importPackage("okio.GzipSource");
//        pool.importPackage("okio.BufferedSink");
//        pool.importPackage("java.nio.charset.Charset");
//        pool.importPackage("java.nio.charset.StandardCharsets");
//        pool.importPackage("java.util.zip.GZIPInputStream");
//        pool.importPackage("java.io.InputStream");
//        pool.importPackage("java.io.InputStreamReader");
//        pool.importPackage("java.io.BufferedReader");
//        pool.importPackage("java.lang.StringBuffer");
//        pool.importPackage("java.io.ByteArrayInputStream");
//    }
//
//    public String CallServerInterceptor_intercept_Before() {
//        return "try{ \n" +
//                " Traffic traffic = new Traffic();\n" +
//                "   MyMap myMap = new MyMap(); \n" +
//                "   traffic.setFrom(\"okhttp3.internal.http.CallServerInterceptor.intercept.before\"); \n" +
//                " RealInterceptorChain realChain_123 = (RealInterceptorChain) $1;\n" +
//                " Request request_123 = realChain_123.request(); \n" +
//                " traffic.setKey(request_123.hashCode() + \"\"); \n" +
//                "  traffic.setReqDate(System.currentTimeMillis()); \n" +
//                "   traffic.setDirection(\"up\"); \n" +
//                "  traffic.setRequestHeaders(myMap); \n" +
//                "   HttpUrl tempUrlObj =  request_123.url();  \n" +
//                "  traffic.setUrl(tempUrlObj.url().toString());  \n" +
//                "  traffic.setHost(tempUrlObj.host()); \n" +
////                "  Call call123 =  realChain_123.call().clone();\n" +
////
//                " Headers headers_123 = request_123.headers(); \n" +
//                " java.util.Collections.UnmodifiableSet headerSet_123 = headers_123.names(); \n" +
//                " List headerList_123 = new ArrayList(headerSet_123);\n " +
//                " for (int i = 0; i < headerList_123.size(); i++){  \n" +
//                " String tempKey_123 = (String) headerList_123.get(i); \n" +
//                " // System.out.println(\"req key: \"+ tempKey_123+\"  --  value: \" +headers_123.get(tempKey_123) ); \n" +
//                "        myMap.put(tempKey_123, headers_123.get(tempKey_123)); \n" +
//                "  }\n" +
//                "  System.out.println(okhttp3.internal.http.CallServerInterceptor.class.getPackage() ); \n" +
//                "  RequestBody  body_123 = request_123.body();\n" +
//                "  String reqBody2Str_123 =\"\";\n" +
//                "  if(body_123!=null){\n" +
//                "  ByteArrayOutputStream byteArrayOutputStream123 = new ByteArrayOutputStream(); \n" +
//                "  BufferedSink bufferedSink123 = Okio.buffer(Okio.sink(byteArrayOutputStream123));   \n" +
//                "        body_123.writeTo(bufferedSink123); \n" +
//                "        bufferedSink123.flush();  \n" +
//                "        bufferedSink123.close();  \n" +
//                "        byte[] bytes123 = byteArrayOutputStream123.toByteArray();\n" +
//                "        reqBody2Str_123 = new String(bytes123, StandardCharsets.UTF_8);\n" +
//                "     //   System.out.println(\"reqBody: \" + reqBody2Str_123);  \n" +
//                "  traffic.setReqBodyLength(body_123.contentLength() ); \n" +
//                "     }  \n" +
//                "  // System.out.println(reqBody2Str_123); \n" +
//                "  traffic.setRequestBody(reqBody2Str_123 ); \n" +
//                "   AgentInfoSendUtil.send(traffic); \n" +
//                " } catch (Exception e123){ \n" +
//                "   e123.printStackTrace(); \n" +
//                "   AgentInfoSendUtil.sendExcepTion(e123); \n" +
//                "}";
//    }
//
//    public String CallServerInterceptor_intercept_After(){
//        return "try{ \n" + "  Headers headers_123 = $_.headers();\n" +
//                " Traffic traffic = new Traffic();\n" +
//                "   traffic.setFrom(\"okhttp3.internal.http.CallServerInterceptor.intercept.after\"); \n" +
//                "   MyMap myMap = new MyMap(); \n" +
//                "   String zipType = \"\"; \n" +
//                "  traffic.setRespDate(System.currentTimeMillis()); \n" +
//                "   traffic.setDirection(\"down\"); \n" +
//                "   traffic.setResponseHeaders(myMap); \n" +
//                "  traffic.setKey($1.request().hashCode() + \"\"); \n" +
//                "            java.util.Collections.UnmodifiableSet respNames_123 = headers_123.names();\n" +
//                "            List respHeaderList_123 = new ArrayList(respNames_123);\n " +
//                " for (int i = 0; i < respHeaderList_123.size(); i++){  \n" +
//                " String tempKey_123 = (String) respHeaderList_123.get(i); \n" +
//                "  if(\"Content-Encoding\".equalsIgnoreCase(tempKey_123)){ \n" +
//                " zipType = headers_123.get(tempKey_123);   " +
//                "} \n" +
//                "    //            System.out.println(\"resp key: \" + tempKey_123 + \" -- value: \" + headers_123.get(tempKey_123));\n" +
//                "        myMap.put(tempKey_123, headers_123.get(tempKey_123)); \n" +
//                "            }\n" +
//                "   ResponseBody respBody_123 = $_.peekBody(Long.MAX_VALUE);\n" +
//                "    String respBody2Str_123 = \"\";\n" +
//
//                "          System.out.println(\" gzip: \"+ zipType);\n" +
//                " if(\"gzip\".equalsIgnoreCase(zipType)){ \n" +
//
//                "   InputStream orgInputStream = respBody_123.byteStream(); \n" +
//                "   orgInputStream = InputStreamUtil.cloneOkHttpInputStream(orgInputStream,traffic); \n" +
//                "       \n" +
//                "    } else{   \n" +
//                "     traffic.setRespBodyLength(respBody_123.source().buffer().size()); \n" +
//                "           respBody2Str_123 = respBody_123.string();  \n" +
//                "          //  System.out.println(\"respBody: \"+respBody2Str_123); \n" +
//                "       traffic.setResponseBody(respBody2Str_123);\n" +
//                " } \n" +
//                "   AgentInfoSendUtil.send(traffic); \n" +
//                " } catch (Exception e123){ \n" +
//                "   e123.printStackTrace(); \n" +
//                "   AgentInfoSendUtil.sendExcepTion(e123); \n" +
//                "}";
//    }
//}
