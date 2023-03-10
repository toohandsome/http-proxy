package io.github.toohandsome.attach.util;

import io.github.toohandsome.attach.AgentMain;

import java.lang.instrument.UnmodifiableClassException;

public class Reset {

    public static void reset() throws UnmodifiableClassException {
        System.out.println(AgentMain.classList.size());
        AgentMain.inst1.removeTransformer(AgentMain.transformer1);
        for (Class aClass : AgentMain.classList) {
            AgentMain.inst1.retransformClasses(aClass);
        }
    }
}
