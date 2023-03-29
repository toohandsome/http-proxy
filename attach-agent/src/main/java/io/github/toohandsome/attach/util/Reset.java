package io.github.toohandsome.attach.util;


import io.github.toohandsome.attach.AgentMain;
import io.github.toohandsome.attach.ReWriteHttpTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.HashMap;
import java.util.Set;

public class Reset {

    public static ReWriteHttpTransformer transformer;
    public static Instrumentation inst;
    // 原始class的逻辑
    public static HashMap<String, Class> classMap = new HashMap();

    public static void reset() throws UnmodifiableClassException {
        inst.removeTransformer(transformer);
        Set<String> strings = classMap.keySet();
        for (String string : strings) {
            inst.retransformClasses(classMap.get(string));
        }
    }
}
