package io.github.toohandsome.httproxy.util;

public class OsUtil {

    static String os = System.getProperty("os.name");

    public static boolean isLinux() {
        return os != null && os.toLowerCase().startsWith("linux");
    }

    public static boolean isWindows() {
        return os != null && os.toLowerCase().startsWith("windows");
    }
}
