package io.github.toohandsome.attach.patch.inner;

import javassist.ClassPool;

public class HttpClientInnerPatch {

    public HttpClientInnerPatch(ClassPool pool) {
        pool.importPackage("java.lang.reflect.Field");
        pool.importPackage("org.apache.http.HttpEntity");
        pool.importPackage("java.io.InputStream");
        pool.importPackage("org.apache.http.Header");
        pool.importPackage("org.apache.http.util.EntityUtils");
        pool.importPackage("org.apache.http.entity.BufferedHttpEntity");
        pool.importPackage("io.github.toohandsome.attach.entity.MyMap");
        pool.importPackage("io.github.toohandsome.attach.util.AgentInfoSendUtil");
        pool.importPackage("io.github.toohandsome.attach.entity.Traffic");
        pool.importPackage("io.github.toohandsome.attach.util.InputStreamUtil");
        pool.importPackage("org.apache.http.client.methods.HttpRequestWrapper");
        pool.importPackage("org.apache.http.client.methods.HttpUriRequest");
        pool.importPackage("org.apache.http.HttpRequest");
        pool.importPackage("org.apache.http.client.methods.HttpRequestBase");
        pool.importPackage("io.github.toohandsome.attach.util.ReUtil");
        pool.importPackage("io.github.toohandsome.attach.util.WhiteListCache");
    }

    public String HttpRequestExecutor_execute_Before(){
        return "try{ \n" +
                " HttpUriRequest request1 = (HttpUriRequest) $1; \n" +
                " for (int i = 0; i < WhiteListCache.whiteList.size(); i++) {\n" +
                "                    String whitePath = WhiteListCache.whiteList.get(i);\n" +
                "                    if (request1.getURI().toString().startsWith(whitePath)) {\n" +
                "                        return input;\n" +
                "                    }\n" +
                "                }\n" +
                " Traffic traffic = new Traffic();\n" +
                "   traffic.setFrom(\"org.apache.http.protocol.HttpRequestExecutor.execute.before\"); \n" +
                "   MyMap myMap = new MyMap(); \n" +
                "    myMap.put($1.getRequestLine().toString(), \"null\");  \n" +
                " //traffic.setUrl(client.getURLFile()); \n" +
                " traffic.setKey($1.hashCode() + \"\"); \n" +
                "  traffic.setReqDate(System.currentTimeMillis()); \n" +
                "   traffic.setDirection(\"up\"); \n" +
                "  traffic.setRequestHeaders(myMap); \n" +

                "  traffic.setUrl(request1.getURI().toString());  \n" +

                "    \n" +
                "   \n" +
                "   Header[]  tempHttpMessageArr =  $1.getAllHeaders();  \n" +
                " for (int i = 0; i < tempHttpMessageArr.length; i++){  \n" +
                " Header tempHeader =  tempHttpMessageArr[i]; \n" +
                "    //           System.out.println(\"req key:  \" + tempHeader.getName() + \" -- value: \" + tempHeader.getValue());\n" +
                "        myMap.put(tempHeader.getName(), tempHeader.getValue()); \n" +
                "            }\n" +
                "   \n" +

                "  try{ " +
                "   Field tempEntity = ReUtil.getField($1.getClass(),\"entity\") ;    \n" +
                "  if(tempEntity!=null){  \n" +
                "    tempEntity.setAccessible(true);   \n" +
                "   HttpEntity tempEntityObj = (HttpEntity)  tempEntity.get($1);  \n" +
                "  if(tempEntityObj!=null){  \n" +
                "   InputStream tempStream =   tempEntityObj.getContent()  ;   \n" +
                "  if(tempStream!=null){   \n" +
                "   tempStream =  InputStreamUtil.cloneHttpClientInputStream(tempStream,traffic ) ;  \n" +
                "    } \n" +
                " }\n" +
                " }\n" +
                "   Field originalField = ReUtil.getField($1.getClass(),\"original\") ;    \n" +
                "  if(originalField!=null){  \n" +
                "   originalField.setAccessible(true);   \n" +
                "   HttpRequest tempRequestObj = (HttpRequest) originalField.get($1);  \n" +
                "  if(tempRequestObj!=null){  \n" +
                "     if( tempRequestObj  instanceof  HttpRequestBase ) {\n" +
                "  HttpRequestBase  tempRequestObj1  =  (HttpRequestBase)  tempRequestObj; \n" +
                "  traffic.setHost(tempRequestObj1.getURI().toString()   ); \n" +
                "    } \n" +
                "    } else{  System.out.println(\"tempRequestObj not instanceof  HttpRequestBase  \" + tempRequestObj); } \n" +
                "    } \n" +
                "   AgentInfoSendUtil.send(traffic); \n" +
                "   }catch (Exception e1) {\n" +
                "            e1.printStackTrace();\n" +
                "        }  \n" +
                "       \n" +
                "       \n" +
                " } catch (Exception e123){ \n" +
                "   e123.printStackTrace(); \n" +
                "   AgentInfoSendUtil.sendExcepTion(e123); \n" +
                "}";
    }


    public String HttpRequestExecutor_execute_After(){
        return "try{ \n" +
                " HttpUriRequest request1 = (HttpUriRequest) $1; \n" +
                " for (int i = 0; i < WhiteListCache.whiteList.size(); i++) {\n" +
                "                    String whitePath = WhiteListCache.whiteList.get(i);\n" +
                "                    if (request1.getURI().toString().startsWith(whitePath)) {\n" +
                "                        return input;\n" +
                "                    }\n" +
                "                }\n" +
                " Traffic traffic = new Traffic();\n" +
                "   traffic.setFrom(\"org.apache.http.protocol.HttpRequestExecutor.execute.after\"); \n" +
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
                "   //            System.out.println(\"resp key:  \" + tempHeader.getName() + \" -- value: \" + tempHeader.getValue());\n" +
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
                "   AgentInfoSendUtil.send(traffic);    \n" +

                "       \n" +

//                        "    System.out.println(\"respBody: \"+EntityUtils.toString( $_.getEntity()));   \n" +
                "       \n" +
                " }" +
                "   }catch (Exception e2) {\n" +
                "            e2.printStackTrace();\n" +
                "        }  \n" +
                "       \n" +
                "       \n" +
                " } catch (Exception e123){ \n" +
                "   e123.printStackTrace(); \n" +
                "   AgentInfoSendUtil.sendExcepTion(e123); \n" +
                "}";
    }
}
