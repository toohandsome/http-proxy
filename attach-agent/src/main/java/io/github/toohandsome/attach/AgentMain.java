package io.github.toohandsome.attach;

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

    static List<String> retransformClassList = new ArrayList<>();

    public static Instrumentation inst1;

    static {
        // inner
        // 请求信息
        retransformClassList.add("sun.net.www.http.HttpClient");
        // 301,302 等
        retransformClassList.add("sun.net.www.protocol.http.HttpURLConnection");
        // 响应信息
        retransformClassList.add("sun.net.www.protocol.http.HttpURLConnection$HttpInputStream");
        // 请求信息
        retransformClassList.add("okhttp3.internal.http.CallServerInterceptor");
        // 响应信息
        retransformClassList.add("okhttp3.internal.http.BridgeInterceptor");
        // 请求和响应信息
        retransformClassList.add("org.apache.http.protocol.HttpRequestExecutor");

        // proxy
//        retransformClassList.add("java.net.URL");
//        retransformClassList.add("org.apache.http.client.config.RequestConfig");
//        retransformClassList.add("okhttp3.OkHttpClient");
//        retransformClassList.add("cn.hutool.http.HttpConnection");
    }


    public static List<Class> classList = new ArrayList<>();
    public static MyTransformer1 transformer1;

    public static void agentmain(String args, Instrumentation inst) throws IOException, UnmodifiableClassException {
        System.out.println("agentmain called");
        ProxyIns.PROXY = null;
        ProxyIns.PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",Integer.valueOf(args)));
        inst1 = inst;
        JarFileHelper.addJarToBootstrap(inst);
        final MyTransformer1 transformer = new MyTransformer1(args);
        transformer1 = transformer;
        inst.addTransformer(transformer, true);

        final Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {
            if (retransformClassList.contains(allLoadedClass.getName())) {
                // 改为 MAP 判断是否存在
                classList.add(allLoadedClass);
                System.out.println(allLoadedClass.getName());
                inst.retransformClasses(allLoadedClass);
            }
        }

        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(10086);
                while (true) {
                    //监听（阻塞）
                    Socket socket = serverSocket.accept();
                    //获取输入流对象
                    InputStream is = socket.getInputStream();
                    //获取数据
                    byte[] bys = new byte[1024];
                    int len;    //用于存储读到的字节个数
                    len = is.read(bys);
                    //输出数据
                    InetAddress inetAdress = socket.getInetAddress();
                    System.out.println(inetAdress.getHostName());
                    System.out.println(new String(bys, 0, len));


                    inst1.removeTransformer(AgentMain.transformer1);
                    for (Class aClass : AgentMain.classList) {
                        inst1.retransformClasses(aClass);
                        System.out.println(aClass.getName() + " reset .. ");
                    }

                    is.close();
                    socket.close();
                    serverSocket.close();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("监听结束");
        }).start();
    }


}