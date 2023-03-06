package io.github.toohandsome.proxy.attach;


import java.lang.instrument.Instrumentation;

public class MyAgent {

    public static void agentmain(String agentArgs, Instrumentation inst)   {
        System.out.println("agentmain called");
        inst.addTransformer(new MyTransformer1(), true);

    }

}
