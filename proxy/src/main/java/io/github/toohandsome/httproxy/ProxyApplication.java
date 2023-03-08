package io.github.toohandsome.httproxy;


import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptInitializer;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.intercept.common.FullRequestIntercept;
import com.github.monkeywie.proxyee.intercept.common.FullResponseIntercept;
import com.github.monkeywie.proxyee.server.HttpProxyServer;
import com.github.monkeywie.proxyee.server.HttpProxyServerConfig;
import com.github.monkeywie.proxyee.util.HttpUtil;
import io.github.toohandsome.httproxy.util.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

@SpringBootApplication
public class ProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
        Utils.loadRoutes();
        HttpProxyServerConfig config = new HttpProxyServerConfig();
        config.setHandleSsl(true);
        new HttpProxyServer()
                .serverConfig(config)
                .proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
                    @Override
                    public void init(HttpProxyInterceptPipeline pipeline) {
                        pipeline.addLast(new FullRequestIntercept() {
                            @Override
                            public boolean match(HttpRequest httpRequest, HttpProxyInterceptPipeline httpProxyInterceptPipeline) {
                                return true;
                            }


                            @Override
                            public void handleRequest(FullHttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) {
                                ByteBuf content = httpRequest.content();
                                //打印请求信息
                                final String uri = httpRequest.uri();
//                                System.out.println("request uri: " + uri);
                                final HttpHeaders headers = httpRequest.headers();
                                for (Map.Entry<String, String> header : headers) {
//                                    System.out.println("request header key: " + header.getKey() + " -- value: " + header.getValue());
                                    if ("host".equals(header.getKey().toLowerCase(Locale.ROOT))){
                                        final String host = header.getValue();
                                    }
                                }
                                final int hashCode = pipeline.getHttpRequest().hashCode();
                                headers.add("req_uid", hashCode);
                                System.out.println("request req_uid :" + hashCode);
//                                System.out.println(content.toString(Charset.defaultCharset()));
                            }
                        });
                        pipeline.addLast(new FullResponseIntercept() {
                            @Override
                            public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                                return true;
                            }

                            @Override
                            public void handleResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                                final HttpHeaders headers = httpResponse.headers();
                                for (Map.Entry<String, String> header : headers) {
//                                    System.out.println("response header key: " + header.getKey() + " -- value: " + header.getValue());
                                }
                                final String req_uid = pipeline.getHttpRequest().headers().get("req_uid");
                                System.out.println("response req_uid :" + req_uid);
                                final int i = httpResponse.content().readableBytes();
//                                System.out.println("bodyLength : " + i);
                                final ByteBuf content = httpResponse.content();
//                                System.out.println(content.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                })
                .start(9999);

    }
}
