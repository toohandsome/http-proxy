package com.yxd.http.agent;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpInterceptor implements InvocationHandler {
    private HttpURLConnection conn;
    private InputStream in;
    private OutputStream out;
    private Map<String, String> requestHeaders = new HashMap<>();
    private byte[] requestBody;

    public HttpInterceptor(URLConnection conn) {
        this.conn = (HttpURLConnection) conn;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("methodName2: " + method.getName());
        if (method.getName().equals("getInputStream")) {
            // 获取响应流时，拦截并记录响应头和响应体的内容

            in = (InputStream) method.invoke(conn, args);
            byte[] buffer = new byte[1024];
            int len = in.read(buffer);
            if (len != -1) {
                byte[] data = Arrays.copyOf(buffer, len);
                System.out.println("Response body: " + new String(data, StandardCharsets.UTF_8));
            }
            Map<String, List<String>> responseHeaders = conn.getHeaderFields();
            System.out.println("Response headers: " + responseHeaders);
            return in;
        } else if (method.getName().equals("getOutputStream")) {
            // 获取请求流时，拦截并记录请求体的内容

            out = (OutputStream) method.invoke(conn, args);
            requestBody = new byte[1024];
            int len = out.toString().getBytes(StandardCharsets.UTF_8).length;
            System.arraycopy(out.toString().getBytes(StandardCharsets.UTF_8), 0, requestBody, 0, len);
            return out;
        } else if (method.getName().equals("setRequestProperty")) {
            // 记录请求头的内容
            requestHeaders.put((String) args[0], (String) args[1]);
            return method.invoke(conn, args);
        } else if (method.getName().equals("connect")) {
            // 发起请求前，拦截并记录请求头和请求体的内容

            System.out.println("Request headers: " + requestHeaders);
            System.out.println("Request body: " + new String(requestBody, StandardCharsets.UTF_8));

            return method.invoke(conn, args);
        } else {
            return method.invoke(conn, args);
        }
    }

    public static void intercept(URLConnection conn) {
        HttpInterceptor interceptor = new HttpInterceptor(conn);
        URLConnection proxy = (URLConnection) Proxy.newProxyInstance(HttpInterceptor.class.getClassLoader(),
                new Class<?>[]{URLConnection.class}, interceptor);

        try {
            Field field = conn.getClass().getDeclaredField("delegate");
            field.setAccessible(true);
            field.set(conn, proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
