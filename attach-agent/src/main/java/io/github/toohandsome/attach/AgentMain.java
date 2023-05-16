package io.github.toohandsome.attach;

import io.github.toohandsome.attach.core.Reset;
import io.github.toohandsome.attach.core.ResetListener;
import io.github.toohandsome.attach.util.*;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author toohandsome
 */
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

        ResetListener.startListener();

    }

}