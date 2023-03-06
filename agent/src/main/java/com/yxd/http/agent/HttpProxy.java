package com.yxd.http.agent;
import java.util.HashMap;
import java.util.Map;
public class HttpProxy {
    private static Map<String, String> proxyMap = new HashMap<>();

    static {
        proxyMap.put("http://example.com/", "http://proxy.example.com/");
        proxyMap.put("https://example.com/", "https://proxy.example.com/");
    }

    public static String getProxyUrl(String url) {
        for (String prefix : proxyMap.keySet()) {
            if (url.startsWith(prefix)) {
                String proxyUrl = proxyMap.get(prefix);
                return url.replaceFirst(prefix, proxyUrl);
            }
        }

        return url;
    }
}
