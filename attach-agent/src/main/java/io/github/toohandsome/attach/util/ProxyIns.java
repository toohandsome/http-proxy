package io.github.toohandsome.attach.util;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class ProxyIns {
    public static final  Proxy PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",9658));
}
