package io.github.toohandsome.httproxy;


import io.github.toohandsome.httproxy.netty.DiscardServer;
import io.netty.channel.socket.SocketChannel;
import io.github.toohandsome.httproxy.util.Utils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * @author toohandsome
 */
@SpringBootApplication
public class ProxyApplication {

    /**
     * java -Xbootclasspath/a:/opt/yanhua/base/jdk/jdk1.8.0_221/lib/tools.jar -jar proxy-1.0.0.jar
     * @param args
     */

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
        Utils.loadRoutes();

        int port = 8009;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (Exception e) {
            }
        }
        try {
            final DiscardServer discardServer = new DiscardServer();
            discardServer.run(port);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
