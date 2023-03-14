package io.github.toohandsome.demo.config;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.*;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;

/**
 * @description：<br>
 * @author: caiyaming
 * @date: 2018-07-31 10:07
 * @since: V1.0.0
 */
public class HttpUtils {
    private static PoolingHttpClientConnectionManager clientConnectionManager = null;
    private static CloseableHttpClient httpClient = null;
    //private static RequestConfig config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
    private static RequestConfig config = null;
    private static HttpRequestRetryHandler httpRequestRetryHandler = null;
    /**
     * 双检锁/双重校验锁, 防止httpClient实例多次
     */
    public static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (HttpUtils.class) {
                if (httpClient == null) {
                    init();
                    httpClient = HttpClients.custom()
                            .setConnectionManager(clientConnectionManager) //连接管理器
                            //.setProxy(new HttpHost("myproxy", 8080))     //设置代理
                            .setDefaultRequestConfig(config)               //默认请求配置
                            .setRetryHandler(httpRequestRetryHandler)      //重试策略
                            .build();
                }
            }
        }
        return httpClient;
    }

    /**
     * 创建httpclient连接池并初始化
     */
    private static void init() {
        try {
            //添加对https的支持，该sslContext没有加载客户端证书
            // 如果需要加载客户端证书，请使用如下sslContext,其中KEYSTORE_FILE和KEYSTORE_PASSWORD分别是你的证书路径和证书密码
            //KeyStore keyStore  =  KeyStore.getInstance(KeyStore.getDefaultType()
            //FileInputStream instream =   new FileInputStream(new File(KEYSTORE_FILE));
            //keyStore.load(instream, KEYSTORE_PASSWORD.toCharArray());
            //SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore,KEYSTORE_PASSWORD.toCharArray())
            // .loadTrustMaterial(null, new TrustSelfSignedStrategy())
            //.build();

            //这里设置信任所有证书
//            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
//                // 信任所有
//                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                    return true;
//                }
//            }).build();
//            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
//                    .register("https", sslsf)
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .build();
            //配置连接池
            clientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            //最大连接
            clientConnectionManager.setMaxTotal(50);
            //默认的每个路由的最大连接数
            clientConnectionManager.setDefaultMaxPerRoute(25);
            //设置到某个路由的最大连接数，会覆盖defaultMaxPerRoute
//            clientConnectionManager.setMaxPerRoute(new HttpRoute(new HttpHost("127.0.0.1", 80)), 150);


            /**
             * socket配置（默认配置 和 某个host的配置）
             */
//            SocketConfig socketConfig = SocketConfig.custom()
//                    .setTcpNoDelay(true)     //是否立即发送数据，设置为true会关闭Socket缓冲，默认为false
//                    .setSoReuseAddress(true) //是否可以在一个进程关闭Socket后，即使它还没有释放端口，其它进程还可以立即重用端口
//                    .setSoTimeout(500)       //接收数据的等待超时时间，单位ms
//                    .setSoLinger(6)         //关闭Socket时，要么发送完所有数据，要么等待60s后，就关闭连接，此时socket.close()是阻塞的
//                    .setSoKeepAlive(true)    //开启监视TCP连接是否有效
//                    .build();
//            clientConnectionManager.setDefaultSocketConfig(socketConfig);
//            clientConnectionManager.setSocketConfig(new HttpHost("somehost", 80), socketConfig);


            /**
             * HTTP connection相关配置（默认配置 和 某个host的配置）
             * 一般不修改HTTP connection相关配置，故不设置
             */
            //消息约束
            MessageConstraints messageConstraints = MessageConstraints.custom()
                    .setMaxHeaderCount(200)
                    .setMaxLineLength(2000)
                    .build();
            //Http connection相关配置
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE)
                    .setCharset(Consts.UTF_8)
                    .setMessageConstraints(messageConstraints)
                    .build();
            //一般不修改HTTP connection相关配置，故不设置
            //connManager.setDefaultConnectionConfig(connectionConfig);
            //connManager.setConnectionConfig(new HttpHost("somehost", 80), ConnectionConfig.DEFAULT);


            // 配置请求的超时设置
            config = RequestConfig.custom()
                    .setConnectTimeout(2 * 1000)         //连接超时时间
                    .setSocketTimeout(2 * 1000)          //读超时时间（等待数据超时时间）
                    .setConnectionRequestTimeout(500)    //从池中获取连接超时时间
                    .setStaleConnectionCheckEnabled(true)//检查是否为陈旧的连接，默认为true，类似testOnBorrow
                    .build();


            /**
             * 重试处理
             * 默认是重试3次
             */
            //禁用重试(参数：retryCount、requestSentRetryEnabled)
            HttpRequestRetryHandler requestRetryHandler = new DefaultHttpRequestRetryHandler(0, false);
            //自定义重试策略
            httpRequestRetryHandler = new HttpRequestRetryHandler() {
                public boolean retryRequest(IOException exception,
                                            int executionCount, HttpContext context) {
                    if (executionCount >= 3) {// 如果已经重试了3次，就放弃
                        return false;
                    }
                    if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                        return true;
                    }
                    if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                        return false;
                    }
                    if (exception instanceof InterruptedIOException) {// 超时
                        return false;
                    }
                    if (exception instanceof UnknownHostException) {// 目标服务器不可达
                        return false;
                    }
                    if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                        return false;
                    }
                    if (exception instanceof SSLException) {// SSL握手异常
                        return false;
                    }
                    HttpClientContext clientContext = HttpClientContext
                            .adapt(context);
                    HttpRequest request = clientContext.getRequest();
                    //Retry if the request is considered idempotent
                    //如果请求类型不是HttpEntityEnclosingRequest，被认为是幂等的，那么就重试
                    //HttpEntityEnclosingRequest指的是有请求体的request，比HttpRequest多一个Entity属性
                    //而常用的GET请求是没有请求体的，POST、PUT都是有请求体的
                    //Rest一般用GET请求获取数据，故幂等，POST用于新增数据，故不幂等
                    if (!(request instanceof HttpEntityEnclosingRequest)) {
                        return true;
                    }
                    return false;
                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}