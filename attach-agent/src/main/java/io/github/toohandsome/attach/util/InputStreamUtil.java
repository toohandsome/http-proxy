package io.github.toohandsome.attach.util;

import io.github.toohandsome.attach.entity.MyMap;
import io.github.toohandsome.attach.entity.Traffic;
import sun.net.www.MessageHeader;
import sun.net.www.http.ChunkedInputStream;
import sun.net.www.http.HttpClient;
import sun.net.www.http.PosterOutputStream;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.nio.charset.StandardCharsets;

public class InputStreamUtil {


    public static void getHttpConnectionRedirectRespInfo(HttpURLConnection httpURLConnection) {
        try {

            if (!httpURLConnection.getInstanceFollowRedirects()) {
                return;
            }
            int respCode = httpURLConnection.getResponseCode();
            if (respCode >= 300 && respCode <= 307 && respCode != 306 && respCode != 304) {

                Class httpURLConnectionClass = null;
                if ("sun.net.www.protocol.https.DelegateHttpsURLConnection".equals(httpURLConnection.getClass().getCanonicalName()) || "com.sun.net.ssl.internal.www.protocol.https.DelegateHttpsURLConnection".equals(httpURLConnection.getClass().getCanonicalName())) {
                    httpURLConnectionClass = httpURLConnection.getClass().getSuperclass().getSuperclass();
                } else if ("sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection".equals(httpURLConnection.getClass().getCanonicalName())) {
                    httpURLConnectionClass = httpURLConnection.getClass().getSuperclass();
                } else {
                    httpURLConnectionClass = httpURLConnection.getClass();
                }
                Traffic traffic = new Traffic();
                traffic.setFrom("getHttpConnectionRedirectRespInfo");
                String respBody = "";
                //System.out.println("respBody: " + respBody);

                if (httpURLConnection != null) {
                    final Field responses = httpURLConnectionClass.getDeclaredField("responses");
                    responses.setAccessible(true);
                    final MessageHeader header = (MessageHeader) responses.get(httpURLConnection);
                    MyMap myMap = printHeader(header, "resp");
                    traffic.setResponseHeaders(myMap);
                }

                Field httpClient = httpURLConnectionClass.getDeclaredField("http");
                httpClient.setAccessible(true);
                traffic.setRespBodyLength(0);
                traffic.setRespDate(System.currentTimeMillis());
                traffic.setDirection("down");
                traffic.setKey(httpClient.get(httpURLConnection).hashCode() + "");
                traffic.setResponseBody(respBody);
                AgentInfoSendUtil.send(traffic);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AgentInfoSendUtil.sendExcepTion(e);
        }
    }

    public static void getHttpConnectionRequestInfo(HttpClient client, MessageHeader header, PosterOutputStream var2) {
        try {
            Class httpClientClass = null;
            if ("sun.net.www.protocol.https.HttpsClient".equals(client.getClass().getCanonicalName())) {
                httpClientClass = client.getClass().getSuperclass();
            } else {
                httpClientClass = client.getClass();
            }
            Field host = httpClientClass.getDeclaredField("host");
            host.setAccessible(true);
            MyMap myMap = printHeader(header, "req");
            Traffic traffic = new Traffic();
            traffic.setFrom("getHttpConnectionRequestInfo");
            traffic.setUrl(client.getURLFile());
            if (var2 != null) {
                traffic.setReqBodyLength(var2.toByteArray().length);
                traffic.setRequestBody(var2.toString());
                // System.out.println("reqBody: " + var2.toString());
            }
            traffic.setReqDate(System.currentTimeMillis());
            traffic.setHost(host.get(client) + "");
            traffic.setDirection("up");
            traffic.setKey(header.hashCode() + "");
            traffic.setMethod("");
            traffic.setRequestHeaders(myMap);
            AgentInfoSendUtil.send(traffic);
        } catch (Exception e) {
            e.printStackTrace();
            AgentInfoSendUtil.sendExcepTion(e);
        }
    }

    public static InputStream cloneHttpConnectionInputStream(InputStream input, HttpURLConnection httpURLConnection) throws Exception {
        try {
            if (input instanceof ChunkedInputStream) {
                return input;
            }
            if (httpURLConnection != null) {
                URL url = httpURLConnection.getURL();
                String s = url.toString();
                for (int i = 0; i < WhiteListCache.whiteList.size(); i++) {
                    String whitePath = WhiteListCache.whiteList.get(i);
                    if (s.startsWith(whitePath)) {
                        return input;
                    }
                }
            }

            ByteArrayOutputStream baos = getByteArrayOutputStream(input);
            Traffic traffic = new Traffic();
            traffic.setFrom("cloneHttpConnectionInputStream");
            byte[] bytes = baos.toByteArray();
            String respBody = new String(bytes, StandardCharsets.UTF_8);
//            System.out.println("respBody: " + respBody);
            if (httpURLConnection != null) {
                Class httpURLConnectionClass = null;
                if ("sun.net.www.protocol.https.DelegateHttpsURLConnection".equals(httpURLConnection.getClass().getCanonicalName()) || "com.sun.net.ssl.internal.www.protocol.https.DelegateHttpsURLConnection".equals(httpURLConnection.getClass().getCanonicalName())) {
                    httpURLConnectionClass = httpURLConnection.getClass().getSuperclass().getSuperclass();
                } else if ("sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection".equals(httpURLConnection.getClass().getCanonicalName())) {
                    httpURLConnectionClass = httpURLConnection.getClass().getSuperclass();
                } else {
                    httpURLConnectionClass = httpURLConnection.getClass();
                }

                final Field responses = httpURLConnectionClass.getDeclaredField("responses");
                responses.setAccessible(true);
                final MessageHeader header = (MessageHeader) responses.get(httpURLConnection);
                MyMap myMap = printHeader(header, "resp");
                traffic.setResponseHeaders(myMap);
                Field httpClient = httpURLConnectionClass.getDeclaredField("requests");
                httpClient.setAccessible(true);
                if (bytes != null) {
                    traffic.setRespBodyLength(bytes.length);
                }
                traffic.setRespDate(System.currentTimeMillis());
                traffic.setDirection("down");
                traffic.setKey(httpClient.get(httpURLConnection).hashCode() + "");
                traffic.setResponseBody(respBody);
                AgentInfoSendUtil.send(traffic);
            }

            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            AgentInfoSendUtil.sendExcepTion(e);
        }
        return input;
    }

    public static InputStream cloneHttpClientInputStream(InputStream input, Traffic traffic) throws Exception {
        try {
            ByteArrayOutputStream baos = getByteArrayOutputStream(input);
            byte[] bytes = baos.toByteArray();
            String respBody = "";
            if (bytes != null) {
                traffic.setReqBodyLength(bytes.length);
            }

            respBody = new String(bytes, StandardCharsets.UTF_8);
            //   System.out.println("reqBody: " + respBody);
            traffic.setRequestBody(respBody);
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            AgentInfoSendUtil.sendExcepTion(e);
        }
        return input;
    }

    public static InputStream cloneHttpClientInputStream1(InputStream input, Traffic traffic, String zipType) throws Exception {
        try {
            ByteArrayOutputStream baos = getByteArrayOutputStream(input);
            byte[] bytes = baos.toByteArray();
            String respBody = "";
            if (bytes != null) {
                traffic.setRespBodyLength(bytes.length);
            }
            if ("gzip".equalsIgnoreCase(zipType)) {
                InputStream input1 = new GZIPInputStream(new ByteArrayInputStream(bytes));
                BufferedReader reader = new BufferedReader(new InputStreamReader(input1, "utf-8"));
                StringBuffer respBodyBuffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    respBodyBuffer.append(line + "\r\n");
                }
                respBody = respBodyBuffer.toString();
            } else {
                respBody = new String(bytes, StandardCharsets.UTF_8);
            }

            //System.out.println("respBody: " + respBody);
            traffic.setResponseBody(respBody);
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            AgentInfoSendUtil.sendExcepTion(e);
        }
        return input;
    }

    private static ByteArrayOutputStream getByteArrayOutputStream(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        return baos;
    }

    public static InputStream cloneOkHttpInputStream(InputStream input, Traffic traffic) throws Exception {
        try {
            ByteArrayOutputStream baos = getByteArrayOutputStream(input);
            byte[] bytes = baos.toByteArray();
            String respBody = "";
            if (bytes != null) {
                traffic.setRespBodyLength(bytes.length);
            }

            GZIPInputStream input1 = new GZIPInputStream(new ByteArrayInputStream(bytes));
            BufferedReader reader = new BufferedReader(new InputStreamReader(input1, "utf-8"));
            StringBuffer respBodyBuffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                respBodyBuffer.append(line + "\r\n");
            }
            respBody = respBodyBuffer.toString();
            //System.out.println("respBody: " + respBody);
            traffic.setResponseBody(respBody);
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            AgentInfoSendUtil.sendExcepTion(e);
        }
        return input;
    }

    private static MyMap printHeader(MessageHeader header, String type) throws NoSuchFieldException, IllegalAccessException {
        Field keys = header.getClass().getDeclaredField("keys");
        Field values = header.getClass().getDeclaredField("values");
        keys.setAccessible(true);
        values.setAccessible(true);
        String[] keysArr = (String[]) keys.get(header);
        String[] valuesArr = (String[]) values.get(header);
        MyMap myMap = new MyMap();
        for (int i = 0; i < keysArr.length; i++) {
            if (keysArr[i] == null && valuesArr[i] == null) {
                continue;
            }
            // System.out.println(type + "Key: " + keysArr[i] + "  --  value: " + valuesArr[i]);
            myMap.put(keysArr[i], valuesArr[i]);
        }
        return myMap;
    }

    public static InputStream cloneHttpConnectionInputStream1(InputStream input, InputStream input2, HttpURLConnection httpURLConnection) throws Exception {

        try {
            if (httpURLConnection != null) {
                URL url = httpURLConnection.getURL();
                String s = url.toString();
                for (String s1 : WhiteListCache.whiteList) {
                    if (s.startsWith(s1)) {
                        return input2;
                    }
                }
            }
            ByteArrayOutputStream baos = getByteArrayOutputStream(input);
            Traffic traffic = new Traffic();
            InputStreamUtil.class.getEnclosingMethod().getName();
            traffic.setFrom("cloneHttpConnectionInputStream1");
            byte[] bytes = baos.toByteArray();
            InputStream input1 = new GZIPInputStream(new ByteArrayInputStream(bytes));
            BufferedReader reader = new BufferedReader(new InputStreamReader(input1, "utf-8"));
            StringBuffer respBody = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                respBody.append(line + "\r\n");
            }
            // System.out.println("respBody: " + respBody);
            if (httpURLConnection != null) {
                Class httpURLConnectionClass = null;
                if ("sun.net.www.protocol.https.DelegateHttpsURLConnection".equals(httpURLConnection.getClass().getCanonicalName()) || "com.sun.net.ssl.internal.www.protocol.https.DelegateHttpsURLConnection".equals(httpURLConnection.getClass().getCanonicalName())) {
                    httpURLConnectionClass = httpURLConnection.getClass().getSuperclass().getSuperclass();
                } else if ("sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection".equals(httpURLConnection.getClass().getCanonicalName())) {
                    httpURLConnectionClass = httpURLConnection.getClass().getSuperclass();
                } else {
                    httpURLConnectionClass = httpURLConnection.getClass();
                }

                final Field responses = httpURLConnectionClass.getDeclaredField("responses");
                responses.setAccessible(true);
                final MessageHeader header = (MessageHeader) responses.get(httpURLConnection);
                MyMap myMap = printHeader(header, "resp");
                traffic.setResponseHeaders(myMap);
                Field httpClient = httpURLConnectionClass.getDeclaredField("http");
                httpClient.setAccessible(true);
                if (bytes != null) {
                    traffic.setRespBodyLength(bytes.length);
                }

                Arrays.stream(httpURLConnection.getClass().getDeclaredFields()).forEach(f -> {
                    f.setAccessible(true);
                    try {
                        Object o = f.get(httpURLConnection);
                        if (o != null) {
                            System.out.println(f.getName() + " ---  " + o.hashCode());
                        } else {
                            System.out.println(f.getName() + " ---  null");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                traffic.setKey(httpClient.get(httpURLConnection).hashCode() + "");
            }
            traffic.setRespDate(System.currentTimeMillis());
            traffic.setDirection("down");
            traffic.setResponseBody(respBody.toString());
            AgentInfoSendUtil.send(traffic);
            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            AgentInfoSendUtil.sendExcepTion(e);
        }
        return input2;
    }

}
