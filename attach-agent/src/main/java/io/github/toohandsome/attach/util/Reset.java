package io.github.toohandsome.attach.util;


import io.github.toohandsome.attach.ReWriteHttpTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class Reset {

    public static ReWriteHttpTransformer transformer;
    public static Instrumentation inst;
    public static void reset() throws UnmodifiableClassException {
//        System.out.println(AgentMain.classList.size());
//        AgentMain.inst1.removeTransformer(AgentMain.transformer1);
//        for (Class aClass : AgentMain.classList) {
//            AgentMain.inst1.retransformClasses(aClass);
//        }
    }
}
