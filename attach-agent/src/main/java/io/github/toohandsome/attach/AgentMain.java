package io.github.toohandsome.attach;

import io.github.toohandsome.attach.classloader.AttachClassloader;
import io.github.toohandsome.attach.entity.AgentInfo;
import io.github.toohandsome.attach.util.*;
//import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class AgentMain {


//    public static Instrumentation inst1;

//    static {
//        // inner
//        // 请求信息
//        retransformClassList.add("sun.net.www.http.HttpClient");
//        // 301,302 等
//        retransformClassList.add("sun.net.www.protocol.http.HttpURLConnection");
//        // 响应信息
//        retransformClassList.add("sun.net.www.protocol.http.HttpURLConnection$HttpInputStream");
//        // 请求信息
//        retransformClassList.add("okhttp3.internal.http.CallServerInterceptor");
//        // 响应信息
//        retransformClassList.add("okhttp3.internal.http.BridgeInterceptor");
//        // 请求和响应信息
//        retransformClassList.add("org.apache.http.protocol.HttpRequestExecutor");
//
//        // proxy
////        retransformClassList.add("java.net.URL");
////        retransformClassList.add("org.apache.http.client.config.RequestConfig");
////        retransformClassList.add("okhttp3.OkHttpClient");
////        retransformClassList.add("cn.hutool.http.HttpConnection");
//    }


//    public static List<Class> classList = new ArrayList<>();
//    public static ReWriteHttpTransformer transformer1;

//    public static void agentmain(String args, Instrumentation inst) throws IOException, UnmodifiableClassException {
//        AgentInfoSendUtil.sendInfo("agent loaded..");
//        List<String> retransformClassList = new ArrayList<>();
//        retransformClassList.add("sun.net.www.http.HttpClient");
//        // 301,302 等
//        retransformClassList.add("sun.net.www.protocol.http.HttpURLConnection");
//        // 响应信息
//        retransformClassList.add("sun.net.www.protocol.http.HttpURLConnection$HttpInputStream");
//        // 请求信息
//        retransformClassList.add("okhttp3.internal.http.CallServerInterceptor");
//        // 响应信息
//        retransformClassList.add("okhttp3.internal.http.BridgeInterceptor");
//        // 请求和响应信息
//        retransformClassList.add("org.apache.http.protocol.HttpRequestExecutor");
////        ProxyIns.PROXY = null;
////        ProxyIns.PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", Integer.valueOf(args)));
////        inst1 = inst;
//        JarFileHelper.addJarToBootstrap(inst);
//        ReWriteHttpTransformer transformer = new ReWriteHttpTransformer(args);
////        transformer1 = transformer;
//        inst.addTransformer(transformer, true);
//        List<Class> classList = new ArrayList<>();
//        final Class[] allLoadedClasses = inst.getAllLoadedClasses();
//        for (Class allLoadedClass : allLoadedClasses) {
//            if (retransformClassList.contains(allLoadedClass.getName())) {
//                // 改为 MAP 判断是否存在
//                classList.add(allLoadedClass);
//                System.out.println(allLoadedClass.getName());
//                inst.retransformClasses(allLoadedClass);
//            }
//        }
//        for (Class aClass : classList) {
//            inst.retransformClasses(aClass);
//        }
//        inst.removeTransformer(transformer);
//
////        transformer1 = null;
//
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                try {
////                    ServerSocket serverSocket = new ServerSocket(10086);
////                    while (true) {
////                        //监听（阻塞）
////                        Socket socket = serverSocket.accept();
////                        //获取输入流对象
////                        InputStream is = socket.getInputStream();
////                        //获取数据
////                        byte[] bys = new byte[1024];
////                        int len;    //用于存储读到的字节个数
////                        len = is.read(bys);
////                        //输出数据
////                        InetAddress inetAddress = socket.getInetAddress();
////                        System.out.println(inetAddress.getHostName());
////                        System.out.println(new String(bys, 0, len));
////
////                        AgentMain.inst1.removeTransformer(AgentMain.transformer1);
////                        AgentMain.transformer1 = null;
////                        for (Class aClass : AgentMain.classList) {
////                            AgentMain.inst1.retransformClasses(aClass);
////                        }
////                        is.close();
////                        socket.close();
////                        serverSocket.close();
////                        break;
////                    }
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////                AgentInfoSendUtil.sendInfo(" 监听结束 .. ");
////
////            }
////        }).start();
//
//    }

    public static void agentmain(String args, Instrumentation inst) {
//        final URL[] urls = new URL[0];
////        urls[0] = new URL("");
//        AttachClassloader attachClassloader = new AttachClassloader(urls);
//        attachClassloader = null;
//        this.
        Thread.currentThread().interrupt();
    }
}