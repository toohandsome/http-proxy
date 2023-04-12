package io.github.toohandsome.attach.config;

import io.github.toohandsome.attach.entity.Traffic;
/**
 * @author hudcan
 */
public class GlobalConfig {
    public static boolean printStack = false;

    public static void printStack(Traffic traffic) {

        if (!printStack) {
            return;
        }
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement stackTraceElement : stackTrace) {
            sb.append("\t").append(stackTraceElement.getClassName());
            sb.append(".").append(stackTraceElement.getMethodName());
            sb.append("(").append(stackTraceElement.getFileName()).append(":");
            sb.append(stackTraceElement.getLineNumber() + ")").append("\n");
        }
        traffic.setStackTrace(sb.toString());
    }
}
