package io.github.toohandsome.attach.config;

import io.github.toohandsome.attach.entity.Traffic;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author hudcan
 */
public class GlobalConfig {
    public static boolean printStack = false;
    public static boolean proxyMode = false;

    public static String listenPort = "10085";
    public static int proxyPort = 10084;

    private static Proxy PROXY = null;

    public synchronized static Proxy getProxy() {
        if (proxyMode && PROXY == null) {
            PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", proxyPort));
        }
        return PROXY;
    }


    public static void printStack(Traffic traffic) {

        if (!printStack) {
            return;
        }
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();

        for (int i = 2; i < stackTrace.length; i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            sb.append("\t").append(stackTraceElement.getClassName());
            sb.append(".").append(stackTraceElement.getMethodName());
            sb.append("(").append(stackTraceElement.getFileName()).append(":");
            sb.append(stackTraceElement.getLineNumber() + ")").append("\n");
        }
        traffic.setStackTrace(sb.toString());
    }
}
