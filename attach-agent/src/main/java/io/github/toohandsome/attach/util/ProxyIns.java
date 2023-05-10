package io.github.toohandsome.attach.util;

import java.net.InetSocketAddress;
import java.net.Proxy;
/**
 * @author hudcan
 */
public class ProxyIns {

    public static Proxy PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));

    public static Proxy getProxy() {
        final Proxy PROXY1 = PROXY;
        return PROXY1;
    }
}
