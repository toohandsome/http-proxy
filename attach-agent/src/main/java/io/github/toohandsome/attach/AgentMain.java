package io.github.toohandsome.attach;

import io.github.toohandsome.attach.entity.AgentInfo;
import io.github.toohandsome.attach.util.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AgentMain {


    static ArrayList<String> retransformClassList = new ArrayList<>();

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


    public static void agentmain(String args, Instrumentation inst) throws IOException, UnmodifiableClassException {
        AgentInfoSendUtil.sendInfo("agent loaded..");

//        ProxyIns.PROXY = null;
//        ProxyIns.PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", Integer.valueOf(args)));
        Reset.inst = inst;
        JarFileHelper.addJarToBootstrap(inst);
        ReWriteHttpTransformer transformer = new ReWriteHttpTransformer(args);
        Reset.transformer = transformer;
        inst.addTransformer(transformer, true);
        final Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {
            if (retransformClassList.contains(allLoadedClass.getName())) {
                inst.retransformClasses(allLoadedClass);
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(10086);
                    //监听（阻塞）
                    Socket socket = serverSocket.accept();
                    //获取输入流对象
                    InputStream is = socket.getInputStream();
                    //获取数据
                    byte[] bys = new byte[1024];
                    int len;    //用于存储读到的字节个数
                    len = is.read(bys);
                    //输出数据
                    AgentInfoSendUtil.sendInfo("agent 收到命令 : " + new String(bys, 0, len));
                    Reset.reset();
                    is.close();
                    socket.close();
                    serverSocket.close();
                    AgentInfoSendUtil.sendInfo("重置增强类,监听结束 .. ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}