package io.github.toohandsome.attach;

import io.github.toohandsome.attach.util.InputStreamUtil;
import org.objectweb.asm.*;
//import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class AgentMain {

    static List<String> retransformClassList = new ArrayList<>();

    static {
        retransformClassList.add("java.net.URL");
        retransformClassList.add("org.apache.http.client.config.RequestConfig");
        retransformClassList.add("okhttp3.OkHttpClient");
        retransformClassList.add("cn.hutool.http.HttpConnection");
    }

    public static void agentmain(String args, Instrumentation inst) throws IOException, UnmodifiableClassException {
        JarFileHelper.addJarToBootstrap(inst);
        System.out.println("agentmain called");
        inst.addTransformer(new MyTransformer1(), true);

        final Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {
            if (retransformClassList.contains(allLoadedClass.getName())) {
                System.out.println(allLoadedClass.getName());
                inst.retransformClasses(allLoadedClass);
            }
        }

    }

}