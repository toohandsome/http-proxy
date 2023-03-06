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

public class AgentMain {

    public static void agentmain(String args, Instrumentation ins) throws IOException, UnmodifiableClassException {
        JarFileHelper.addJarToBootstrap(ins);
        System.out.println("agentmain called");
        ins.addTransformer(new MyTransformer1(), true);
        ins.retransformClasses(java.net.URL.class);

    }

}