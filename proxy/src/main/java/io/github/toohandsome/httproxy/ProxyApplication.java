package io.github.toohandsome.httproxy;


import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptInitializer;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.intercept.common.FullRequestIntercept;
import com.github.monkeywie.proxyee.intercept.common.FullResponseIntercept;
import com.github.monkeywie.proxyee.server.HttpProxyServer;
import com.github.monkeywie.proxyee.server.HttpProxyServerConfig;
import io.github.toohandsome.httproxy.core.TrafficQueueProcess;
import io.github.toohandsome.httproxy.entity.Traffic;
import io.github.toohandsome.httproxy.util.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SpringBootApplication
public class ProxyApplication {


    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
        Utils.loadRoutes();
        HttpProxyServerConfig config = new HttpProxyServerConfig();
        config.setHandleSsl(false);
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
                                Traffic traffic = new Traffic();
                                traffic.setDirection("up");
                                traffic.setReqDate(System.currentTimeMillis());
                                ByteBuf content = httpRequest.content();
                                final int bodyLength = content.readableBytes();
                                traffic.setReqBodyLength(bodyLength);
                                final String uri = httpRequest.uri();
                                traffic.setUrl(uri);
                                traffic.setMethod(httpRequest.method().name());
                                final HttpHeaders headers = httpRequest.headers();
                                HashMap<String, String> requestHeaders = new HashMap<>(22);
                                for (Map.Entry<String, String> header : headers) {
                                    if ("host".equals(header.getKey().toLowerCase(Locale.ROOT))) {
                                        final String host = header.getValue();
                                        traffic.setHost(host);
                                    }
                                    requestHeaders.put(header.getKey(), header.getValue());
                                }
                                traffic.setRequestHeaders(requestHeaders);
                                final int hashCode = pipeline.getHttpRequest().hashCode();
                                headers.add("req_uid", hashCode);
                                traffic.setKey(hashCode + "");
                                traffic.setRequestBody(content.toString(Charset.defaultCharset()));
                                TrafficQueueProcess.trafficQueue.offer(traffic);
                            }
                        });
                        pipeline.addLast(new FullResponseIntercept() {
                            @Override
                            public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                                return true;
                            }

                            @Override
                            public void handleResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                                Traffic traffic = new Traffic();
                                traffic.setRespDate(System.currentTimeMillis());
                                traffic.setDirection("down");
                                final HttpHeaders headers = httpResponse.headers();
                                HashMap<String, String> requestHeaders = new HashMap<>(22);
                                for (Map.Entry<String, String> header : headers) {
                                    requestHeaders.put(header.getKey(), header.getValue());
                                }
                                traffic.setResponseHeaders(requestHeaders);
                                final String req_uid = pipeline.getHttpRequest().headers().get("req_uid");
                                traffic.setKey(req_uid);
                                final int bodyLength = httpResponse.content().readableBytes();
                                traffic.setRespBodyLength(bodyLength);
                                final ByteBuf content = httpResponse.content();
                                traffic.setResponseBody(content.toString(Charset.defaultCharset()));
                                TrafficQueueProcess.trafficQueue.offer(traffic);
                            }
                        });
                    }
                })
                .start(9658);


    }
}
