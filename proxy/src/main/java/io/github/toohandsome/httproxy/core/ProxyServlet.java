/*
 * Copyright MITRE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.toohandsome.httproxy.core;

import com.alibaba.fastjson2.JSON;
import io.github.toohandsome.httproxy.controller.RouteController;
import io.github.toohandsome.httproxy.entity.Route;
import io.github.toohandsome.httproxy.entity.Rule;
import io.github.toohandsome.httproxy.util.SpringUtil;
import io.github.toohandsome.httproxy.util.Utils;
import jodd.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;

import org.apache.http.client.methods.AbstractExecutionAwareRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.HeaderGroup;
import org.apache.http.util.EntityUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An HTTP reverse proxy/gateway servlet. It is designed to be extended for customization
 * if desired. Most of the work is handled by
 * <a href="http://hc.apache.org/httpcomponents-client-ga/">Apache HttpClient</a>.
 * <p>
 * There are alternatives to a servlet based proxy such as Apache mod_proxy if that is available to you. However
 * this servlet is easily customizable by Java, secure-able by your web application's security (e.g. spring-security),
 * portable across servlet engines, and is embeddable into another web application.
 * </p>
 * <p>
 * Inspiration: http://httpd.apache.org/docs/2.0/mod/mod_proxy.html
 * </p>
 *
 * @author David Smiley dsmiley@apache.org
 */
@Slf4j
public class ProxyServlet extends HttpServlet {

    /* INIT PARAMETER NAME CONSTANTS */

    /**
     * A boolean parameter name to enable logging of input and target URLs to the servlet log.
     */
    public static final String P_LOG = "log";

    /**
     * A boolean parameter name to enable forwarding of the client IP
     */
    public static final String P_FORWARDEDFOR = "forwardip";

    /**
     * A boolean parameter name to keep HOST parameter as-is
     */
    public static final String P_PRESERVEHOST = "preserveHost";

    /**
     * A boolean parameter name to keep COOKIES as-is
     */
    public static final String P_PRESERVECOOKIES = "preserveCookies";

    /**
     * A boolean parameter name to keep COOKIE path as-is
     */
    public static final String P_PRESERVECOOKIEPATH = "preserveCookiePath";

    /**
     * A boolean parameter name to have auto-handle redirects
     */
    public static final String P_HANDLEREDIRECTS = "http.protocol.handle-redirects"; // ClientPNames.HANDLE_REDIRECTS

    /**
     * An integer parameter name to set the socket connection timeout (millis)
     */
    public static final String P_CONNECTTIMEOUT = "http.socket.timeout"; // CoreConnectionPNames.SO_TIMEOUT

    /**
     * An integer parameter name to set the socket read timeout (millis)
     */
    public static final String P_READTIMEOUT = "http.read.timeout";

    /**
     * An integer parameter name to set the connection request timeout (millis)
     */
    public static final String P_CONNECTIONREQUESTTIMEOUT = "http.connectionrequest.timeout";

    /**
     * An integer parameter name to set max connection number
     */
    public static final String P_MAXCONNECTIONS = "http.maxConnections";

    /**
     * A boolean parameter whether to use JVM-defined system properties to configure various networking aspects.
     */
    public static final String P_USESYSTEMPROPERTIES = "useSystemProperties";

    /**
     * A boolean parameter to enable handling of compression in the servlet. If it is false, compressed streams are passed through unmodified.
     */
    public static final String P_HANDLECOMPRESSION = "handleCompression";

    /**
     * The parameter name for the target (destination) URI to proxy to.
     */
    public static final String P_TARGET_URI = "targetUri";

    protected static final String ATTR_TARGET_URI =
            ProxyServlet.class.getSimpleName() + ".targetUri";
    protected static final String ATTR_TARGET_HOST =
            ProxyServlet.class.getSimpleName() + ".targetHost";

    /* MISC */

    protected boolean doLog = false;
    protected boolean doForwardIP = true;
    /**
     * User agents shouldn't send the url fragment but what if it does?
     */
    protected boolean doSendUrlFragment = true;
    protected boolean doPreserveHost = false;
    protected boolean doPreserveCookies = false;
    protected boolean doPreserveCookiePath = false;
    protected boolean doHandleRedirects = false;
    protected boolean useSystemProperties = true;
    protected boolean doHandleCompression = false;
    protected int connectTimeout = -1;
    protected int readTimeout = -1;
    protected int connectionRequestTimeout = -1;
    protected int maxConnections = -1;

    //These next 3 are cached here, and should only be referred to in initialization logic. See the
    // ATTR_* parameters.
    /**
     * From the configured parameter "targetUri".
     */
    public String targetUri;
    protected URI targetUriObj;//new URI(targetUri)
    protected HttpHost targetHost;//URIUtils.extractHost(targetUriObj);

    private HttpClient proxyClient;

    public List<Rule> ruleList = new ArrayList<>();

    @Override
    public String getServletInfo() {
        return "A proxy servlet by David Smiley, dsmiley@apache.org";
    }


    protected String getTargetUri(HttpServletRequest servletRequest) {
        return (String) servletRequest.getAttribute(ATTR_TARGET_URI);
    }

    protected HttpHost getTargetHost(HttpServletRequest servletRequest) {
        return (HttpHost) servletRequest.getAttribute(ATTR_TARGET_HOST);
    }

    public HashMap<String, String> config = new HashMap<>();

    /**
     * Reads a configuration parameter. By default it reads servlet init parameters but
     * it can be overridden.
     */
    protected String getConfigParam(String key) {
        return config.get(key);
    }

    @Override
    public void init() throws ServletException {
        String doLogStr = getConfigParam(P_LOG);
        if (doLogStr != null) {
            this.doLog = Boolean.parseBoolean(doLogStr);
        }

        String doForwardIPString = getConfigParam(P_FORWARDEDFOR);
        if (doForwardIPString != null) {
            this.doForwardIP = Boolean.parseBoolean(doForwardIPString);
        }

        String preserveHostString = getConfigParam(P_PRESERVEHOST);
        if (preserveHostString != null) {
            this.doPreserveHost = Boolean.parseBoolean(preserveHostString);
        }

        String preserveCookiesString = getConfigParam(P_PRESERVECOOKIES);
        if (preserveCookiesString != null) {
            this.doPreserveCookies = Boolean.parseBoolean(preserveCookiesString);
        }

        String preserveCookiePathString = getConfigParam(P_PRESERVECOOKIEPATH);
        if (preserveCookiePathString != null) {
            this.doPreserveCookiePath = Boolean.parseBoolean(preserveCookiePathString);
        }

        String handleRedirectsString = getConfigParam(P_HANDLEREDIRECTS);
        if (handleRedirectsString != null) {
            this.doHandleRedirects = Boolean.parseBoolean(handleRedirectsString);
        }

        String connectTimeoutString = getConfigParam(P_CONNECTTIMEOUT);
        if (connectTimeoutString != null) {
            this.connectTimeout = Integer.parseInt(connectTimeoutString);
        }

        String readTimeoutString = getConfigParam(P_READTIMEOUT);
        if (readTimeoutString != null) {
            this.readTimeout = Integer.parseInt(readTimeoutString);
        }

        String connectionRequestTimeout = getConfigParam(P_CONNECTIONREQUESTTIMEOUT);
        if (connectionRequestTimeout != null) {
            this.connectionRequestTimeout = Integer.parseInt(connectionRequestTimeout);
        }

        String maxConnections = getConfigParam(P_MAXCONNECTIONS);
        if (maxConnections != null) {
            this.maxConnections = Integer.parseInt(maxConnections);
        }

        String useSystemPropertiesString = getConfigParam(P_USESYSTEMPROPERTIES);
        if (useSystemPropertiesString != null) {
            this.useSystemProperties = Boolean.parseBoolean(useSystemPropertiesString);
        }

        String doHandleCompression = getConfigParam(P_HANDLECOMPRESSION);
        if (doHandleCompression != null) {
            this.doHandleCompression = Boolean.parseBoolean(doHandleCompression);
        }

        initTarget();//sets target*

        proxyClient = createHttpClient();
    }

    /**
     * Sub-classes can override specific behaviour of {@link RequestConfig}.
     */
    protected RequestConfig buildRequestConfig() {
//        HttpHost proxy = new HttpHost("127.0.0.1", 8888);
        return RequestConfig.custom()
//                .setProxy(proxy)
                .setRedirectsEnabled(doHandleRedirects)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES) // we handle them in the servlet instead
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(readTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();
    }

    /**
     * Sub-classes can override specific behaviour of {@link SocketConfig}.
     */
    protected SocketConfig buildSocketConfig() {

        if (readTimeout < 1) {
            return null;
        }

        return SocketConfig.custom()
                .setSoTimeout(readTimeout)
                .build();
    }

    protected void initTarget() throws ServletException {
//        targetUri = getConfigParam(P_TARGET_URI);
        if (targetUri == null) {
            throw new ServletException(P_TARGET_URI + " is required.");
        }
        //test it's valid
        try {
            targetUriObj = new URI(targetUri);
        } catch (Exception e) {
            throw new ServletException("Trying to process targetUri init parameter: " + e, e);
        }
        targetHost = URIUtils.extractHost(targetUriObj);
    }

    protected void resetTarget(String targetUrl) throws ServletException {
        this.targetUri = targetUrl;
        if (targetUri == null) {
            throw new ServletException(P_TARGET_URI + " is required.");
        }
        //test it's valid
        try {
            targetUriObj = new URI(targetUri);
        } catch (Exception e) {
            throw new ServletException("Trying to process targetUri init parameter: " + e, e);
        }
        targetHost = URIUtils.extractHost(targetUriObj);
    }

    /**
     * Called from {@link #init(javax.servlet.ServletConfig)}.
     * HttpClient offers many opportunities for customization.
     * In any case, it should be thread-safe.
     */
    protected HttpClient createHttpClient() {
        HttpClientBuilder clientBuilder = getHttpClientBuilder()
                .setDefaultRequestConfig(buildRequestConfig())
                .setDefaultSocketConfig(buildSocketConfig());

        clientBuilder.setMaxConnTotal(maxConnections);
        clientBuilder.setMaxConnPerRoute(maxConnections);
        if (!doHandleCompression) {
            clientBuilder.disableContentCompression();
        }

        if (useSystemProperties) {
            clientBuilder = clientBuilder.useSystemProperties();
        }
        return buildHttpClient(clientBuilder);
    }

    /**
     * Creates a HttpClient from the given builder. Meant as postprocessor
     * to possibly adapt the client builder prior to creating the
     * HttpClient.
     *
     * @param clientBuilder pre-configured client builder
     * @return HttpClient
     */
    protected HttpClient buildHttpClient(HttpClientBuilder clientBuilder) {
        return clientBuilder.build();
    }

    /**
     * Creates a {@code HttpClientBuilder}. Meant as preprocessor to possibly
     * adapt the client builder prior to any configuration got applied.
     *
     * @return HttpClient builder
     */
    protected HttpClientBuilder getHttpClientBuilder() {
        return HttpClientBuilder.create();
    }

    /**
     * The http client used.
     *
     * @see #createHttpClient()
     */
    protected HttpClient getProxyClient() {
        return proxyClient;
    }

    @Override
    public void destroy() {
        //Usually, clients implement Closeable:
        if (proxyClient instanceof Closeable) {
            try {
                ((Closeable) proxyClient).close();
            } catch (IOException e) {
                log("While destroying servlet, shutting down HttpClient: " + e, e);
            }
        } else {
            //Older releases require we do this:
            if (proxyClient != null) {
                proxyClient.getConnectionManager().shutdown();
            }
        }
        super.destroy();
    }

    AtomicLong atomicLong = new AtomicLong(1);
    AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
            throws ServletException, IOException {

        String accessId = atomicLong.getAndIncrement() + "";
        logger.info(this.getServletName() + " , " + accessId + " , " + servletRequest.getRequestURL());
        // http://abc/abc.thml
        // http://abc:9090/abc.thml
        // http://abc/
        // http://abc
        Route currentRoute = null;
        boolean routrMatch = false;
        for (Route route : Utils.routes) {
            final String prefix = route.getPrefix();
            routrMatch = pathMatcher.match(prefix, servletRequest.getRequestURI());
            logger.info(route.getName() + ": " + prefix + " match : " + servletRequest.getRequestURI() + " , result:" + routrMatch);
            if (routrMatch) {
                resetTarget(route.getRemote());
                currentRoute = route;
                break;
            }
        }
        if (!routrMatch){
            currentRoute = Route.
        }

        //  优先处理 页面请求,不然在全部转发情况下 无法访问页面
        String pathInfo = servletRequest.getPathInfo();
        if (pathInfo != null && (pathInfo.startsWith("/routeView/") || "/favicon.ico".equals(pathInfo) || "/error".equals(pathInfo))) {
            if ("/error".equals(pathInfo)) {
                servletResponse.getOutputStream().write("error".getBytes(StandardCharsets.UTF_8));
            } else if (pathInfo.contains("/api")) {
                try {
                    RouteController routeController = SpringUtil.getBean(RouteController.class);
                    String methodName = pathInfo.split("/")[3];
                    Method method = routeController.getClass().getDeclaredMethod(methodName, Route.class);
                    String reqStr = ServletUtil.readRequestBodyFromStream(servletRequest);
                    Route route = JSON.parseObject(reqStr, Route.class);
                    Object invoke = method.invoke(routeController, route);
                    servletResponse.setContentType("application/json; charset=utf-8");
                    servletResponse.getOutputStream().write(JSON.toJSONString(invoke).getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                File file = ResourceUtils.getFile("classpath:static/" + pathInfo);
                byte[] data = Files.readAllBytes(file.toPath());
                if (pathInfo.endsWith("html")) {
                    servletResponse.setContentType("text/html; charset=utf-8");
                } else if (pathInfo.endsWith("css")) {
                    servletResponse.setContentType("text/css");
                } else if (pathInfo.endsWith("js")) {
                    servletResponse.setContentType("text/javascript; charset=utf-8");
                } else if (pathInfo.endsWith("ico")) {
                    servletResponse.setContentType("image/x-icon");
                }
                servletResponse.setContentLength(data.length);
                servletResponse.getOutputStream().write(data);
            }
            return;
        }


        //initialize request attributes from caches if unset by a subclass by this point
        if (servletRequest.getAttribute(ATTR_TARGET_URI) == null) {
            servletRequest.setAttribute(ATTR_TARGET_URI, targetUri);
        }
        if (servletRequest.getAttribute(ATTR_TARGET_HOST) == null) {
            servletRequest.setAttribute(ATTR_TARGET_HOST, targetHost);
        }

        // Make the Request
        //note: we won't transfer the protocol version because I'm not sure it would truly be compatible
        String method = servletRequest.getMethod();
        String proxyRequestUri = rewriteUrlFromRequest(servletRequest);
        HttpRequest proxyRequest;
        //spec: RFC 2616, sec 4.3: either of these two headers signal that there is a message body.
        if (servletRequest.getHeader(HttpHeaders.CONTENT_LENGTH) != null ||
                servletRequest.getHeader(HttpHeaders.TRANSFER_ENCODING) != null) {
            proxyRequest = newProxyRequestWithEntity(method, proxyRequestUri, servletRequest, accessId);
        } else {
            proxyRequest = new BasicHttpRequest(method, proxyRequestUri);
        }

        copyRequestHeaders(servletRequest, proxyRequest, accessId);

        setXForwardedForHeader(servletRequest, proxyRequest);

        HttpResponse proxyResponse = null;
        try {
            // Execute the request
            proxyResponse = doExecute(servletRequest, servletResponse, proxyRequest);

            // Process the response:

            // Pass the response code. This method with the "reason phrase" is deprecated but it's the
            //   only way to pass the reason along too.
            int statusCode = proxyResponse.getStatusLine().getStatusCode();
            //noinspection deprecation
            servletResponse.setStatus(statusCode, proxyResponse.getStatusLine().getReasonPhrase());

            // Copying response headers to make sure SESSIONID or other Cookie which comes from the remote
            // server will be saved in client when the proxied url was redirected to another one.
            // See issue [#51](https://github.com/mitre/HTTP-Proxy-Servlet/issues/51)
            copyResponseHeaders(proxyResponse, servletRequest, servletResponse);


            if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
                // 304 needs special handling.  See:
                // http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
                // Don't send body entity/content!
                servletResponse.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
            } else {
                // Send the content to the client
                copyResponseEntity(proxyResponse, servletResponse, proxyRequest, servletRequest, accessId);
            }

        } catch (Exception e) {
            handleRequestException(proxyRequest, proxyResponse, e);
        } finally {
            // make sure the entire entity was consumed, so the connection is released
            if (proxyResponse != null) {
                EntityUtils.consumeQuietly(proxyResponse.getEntity());
            }
            //Note: Don't need to close servlet outputStream:
            // http://stackoverflow.com/questions/1159168/should-one-call-close-on-httpservletresponse-getoutputstream-getwriter
        }
    }

    public String getEntityContent(HttpEntity entity) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"))) {
            String str = "";
            StringBuilder sb = new StringBuilder();
            while ((str = reader.readLine()) != null) {
                sb.append(str).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    protected void handleRequestException(HttpRequest proxyRequest, HttpResponse proxyResonse, Exception e) throws ServletException, IOException {
        //abort request, according to best practice with HttpClient
        if (proxyRequest instanceof AbstractExecutionAwareRequest) {
            AbstractExecutionAwareRequest abortableHttpRequest = (AbstractExecutionAwareRequest) proxyRequest;
            abortableHttpRequest.abort();
        }
        // If the response is a chunked response, it is read to completion when
        // #close is called. If the sending site does not timeout or keeps sending,
        // the connection will be kept open indefinitely. Closing the respone
        // object terminates the stream.
        if (proxyResonse instanceof Closeable) {
            ((Closeable) proxyResonse).close();
        }
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        if (e instanceof ServletException) {
            throw (ServletException) e;
        }
        //noinspection ConstantConditions
        if (e instanceof IOException) {
            throw (IOException) e;
        }
        throw new RuntimeException(e);
    }

    protected HttpResponse doExecute(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
                                     HttpRequest proxyRequest) throws IOException {
        if (doLog) {
            log("proxy " + servletRequest.getMethod() + " uri: " + servletRequest.getRequestURI() + " -- " +
                    proxyRequest.getRequestLine().getUri());
        }
        return proxyClient.execute(getTargetHost(servletRequest), proxyRequest);
    }

    protected HttpRequest newProxyRequestWithEntity(String method, String proxyRequestUri,
                                                    HttpServletRequest servletRequest, String accessId)
            throws IOException {
        HttpEntityEnclosingRequest eProxyRequest =
                new BasicHttpEntityEnclosingRequest(method, proxyRequestUri);
        // Add the input entity (streamed)
        //  note: we don't bother ensuring we close the servletInputStream since the container handles it
        InputStream inputStream = servletRequest.getInputStream();
        // 修改请求体
        List<Rule> rules = Rule.getRule(ruleList, 1);
        long contentLength = 0;
        if (!rules.isEmpty()) {
            Rule rule = rules.get(0);
            String content = rule.getContent();
            if (StringUtils.hasText(rule.getSource())) {
                String sourceContent = ServletUtil.readRequestBodyFromStream(servletRequest);
                logger.info(this.getServletName() + " , " + accessId + " , 原始请求内容:" + sourceContent);
                String replace = sourceContent.replace(content, content);
                byte[] bytes = replace.getBytes(servletRequest.getCharacterEncoding());
                inputStream = new ByteArrayInputStream(bytes);
            } else {
                byte[] bytes = content.getBytes(servletRequest.getCharacterEncoding());
                contentLength = bytes.length;
                inputStream = new ByteArrayInputStream(bytes);
            }
        }

        if (contentLength == 0) {
            eProxyRequest.setEntity(
                    new InputStreamEntity(inputStream, getContentLength(servletRequest)));
        } else {
            eProxyRequest.setEntity(
                    new InputStreamEntity(inputStream, contentLength));
        }

        return eProxyRequest;
    }

    // Get the header value as a long in order to more correctly proxy very large requests
    private long getContentLength(HttpServletRequest request) {
        String contentLengthHeader = request.getHeader("Content-Length");
        if (contentLengthHeader != null) {
            return Long.parseLong(contentLengthHeader);
        }
        return -1L;
    }

    protected void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            log(e.getMessage(), e);
        }
    }

    /**
     * These are the "hop-by-hop" headers that should not be copied.
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html
     * I use an HttpClient HeaderGroup class instead of Set&lt;String&gt; because this
     * approach does case insensitive lookup faster.
     */
    protected static final HeaderGroup hopByHopHeaders;

    static {
        hopByHopHeaders = new HeaderGroup();
        String[] headers = new String[]{
                "Connection", "Keep-Alive", "Proxy-Authenticate", "Proxy-Authorization",
                "TE", "Trailers", "Transfer-Encoding", "Upgrade"};
        for (String header : headers) {
            hopByHopHeaders.addHeader(new BasicHeader(header, null));
        }
    }

    /**
     * Copy request headers from the servlet client to the proxy request.
     * This is easily overridden to add your own.
     * <p>
     * 处理请求头
     */
    protected void copyRequestHeaders(HttpServletRequest servletRequest, HttpRequest proxyRequest, String accessId) {
        // Get an Enumeration of all of the header names sent by the client
        @SuppressWarnings("unchecked")
        Enumeration<String> enumerationOfHeaderNames = servletRequest.getHeaderNames();
        while (enumerationOfHeaderNames.hasMoreElements()) {
            String headerName = enumerationOfHeaderNames.nextElement();
            //logger.info(headerName + ": " + servletRequest.getHeader(headerName));
            // 删除和修改的 跳过原始的添加
            List<Rule> headerRule1 = Rule.getRule(ruleList, -10, 0);
            if (headerRule1.stream().filter(rule -> rule.getHeaderName().equalsIgnoreCase(headerName)).count() >= 1) {
                continue;
            }
            copyRequestHeader(servletRequest, proxyRequest, headerName);
        }
        // 增加和修改的 手动添加
        // TODO 替换支持正则
        List<Rule> headerRule2 = Rule.getRule(ruleList, 0, 10);
        for (Rule rule : headerRule2) {
            proxyRequest.addHeader(rule.getHeaderName(), rule.getContent());
        }
        logger.info(this.getServletName() + " , " + accessId + " , proxyRequest headers: " + JSON.toJSONString(proxyRequest.getAllHeaders()));
    }

    /**
     * Copy a request header from the servlet client to the proxy request.
     * This is easily overridden to filter out certain headers if desired.
     */
    protected void copyRequestHeader(HttpServletRequest servletRequest, HttpRequest proxyRequest,
                                     String headerName) {

        //Instead the content-length is effectively set via InputStreamEntity
        if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
            return;
        }
        if (hopByHopHeaders.containsHeader(headerName)) {
            return;
        }
        // If compression is handled in the servlet, apache http client needs to
        // control the Accept-Encoding header, not the client
        if (doHandleCompression && headerName.equalsIgnoreCase(HttpHeaders.ACCEPT_ENCODING)) {
            return;
        }

        @SuppressWarnings("unchecked")
        Enumeration<String> headers = servletRequest.getHeaders(headerName);
        while (headers.hasMoreElements()) {//sometimes more than one value
            String headerValue = headers.nextElement();
            // In case the proxy host is running multiple virtual servers,
            // rewrite the Host header to ensure that we get content from
            // the correct virtual server
            if (!doPreserveHost && headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
                HttpHost host = getTargetHost(servletRequest);
                headerValue = host.getHostName();
                if (host.getPort() != -1) {
                    headerValue += ":" + host.getPort();
                }
            } else if (!doPreserveCookies && headerName.equalsIgnoreCase(org.apache.http.cookie.SM.COOKIE)) {
                headerValue = getRealCookie(headerValue);
            }
            proxyRequest.addHeader(headerName, headerValue);
        }
    }

    private void setXForwardedForHeader(HttpServletRequest servletRequest,
                                        HttpRequest proxyRequest) {
        if (doForwardIP) {
            String forHeaderName = "X-Forwarded-For";
            String forHeader = servletRequest.getRemoteAddr();
            String existingForHeader = servletRequest.getHeader(forHeaderName);
            if (existingForHeader != null) {
                forHeader = existingForHeader + ", " + forHeader;
            }
            proxyRequest.setHeader(forHeaderName, forHeader);

            String protoHeaderName = "X-Forwarded-Proto";
            String protoHeader = servletRequest.getScheme();
            proxyRequest.setHeader(protoHeaderName, protoHeader);
        }
    }

    /**
     * Copy proxied response headers back to the servlet client.
     */
    protected void copyResponseHeaders(HttpResponse proxyResponse, HttpServletRequest servletRequest,
                                       HttpServletResponse servletResponse) {
        for (Header header : proxyResponse.getAllHeaders()) {
            if ("content-length".equalsIgnoreCase(header.getName())) {
                continue;
            }
            // 删除和修改的 跳过原始的添加
            List<Rule> headerRule1 = Rule.getRule(ruleList, -12, 2);
            if (headerRule1.stream().filter(rule -> rule.getHeaderName().equalsIgnoreCase(header.getName())).count() >= 1) {
                continue;
            }
            copyResponseHeader(servletRequest, servletResponse, header);
        }
        // 增加和修改的 手动添加
        // TODO 替换支持正则
        List<Rule> headerRule2 = Rule.getRule(ruleList, 2, 12);
        for (Rule rule : headerRule2) {
            servletResponse.addHeader(rule.getHeaderName(), rule.getContent());
        }
    }

    /**
     * Copy a proxied response header back to the servlet client.
     * This is easily overwritten to filter out certain headers if desired.
     */
    protected void copyResponseHeader(HttpServletRequest servletRequest,
                                      HttpServletResponse servletResponse, Header header) {
        String headerName = header.getName();

        if (hopByHopHeaders.containsHeader(headerName)) {
            return;
        }
        String headerValue = header.getValue();
        if (headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE) ||
                headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE2)) {
            copyProxyCookie(servletRequest, servletResponse, headerValue);
        } else if (headerName.equalsIgnoreCase(HttpHeaders.LOCATION)) {
            // LOCATION Header may have to be rewritten.
            servletResponse.addHeader(headerName, rewriteUrlFromResponse(servletRequest, headerValue));
        } else {
            servletResponse.addHeader(headerName, headerValue);
        }
    }

    /**
     * Copy cookie from the proxy to the servlet client.
     * Replaces cookie path to local path and renames cookie to avoid collisions.
     */
    protected void copyProxyCookie(HttpServletRequest servletRequest,
                                   HttpServletResponse servletResponse, String headerValue) {
        for (HttpCookie cookie : HttpCookie.parse(headerValue)) {
            Cookie servletCookie = createProxyCookie(servletRequest, cookie);
            servletResponse.addCookie(servletCookie);
        }
    }

    /**
     * Creates a proxy cookie from the original cookie.
     *
     * @param servletRequest original request
     * @param cookie         original cookie
     * @return proxy cookie
     */
    protected Cookie createProxyCookie(HttpServletRequest servletRequest, HttpCookie cookie) {
        String proxyCookieName = getProxyCookieName(cookie);
        Cookie servletCookie = new Cookie(proxyCookieName, cookie.getValue());
        servletCookie.setPath(this.doPreserveCookiePath ?
                cookie.getPath() : // preserve original cookie path
                buildProxyCookiePath(servletRequest) //set to the path of the proxy servlet
        );
        servletCookie.setComment(cookie.getComment());
        servletCookie.setMaxAge((int) cookie.getMaxAge());
        // don't set cookie domain
        servletCookie.setSecure(servletRequest.isSecure() && cookie.getSecure());
        servletCookie.setVersion(cookie.getVersion());
        servletCookie.setHttpOnly(cookie.isHttpOnly());
        return servletCookie;
    }

    /**
     * Set cookie name prefixed with a proxy value so it won't collide with other cookies.
     *
     * @param cookie cookie to get proxy cookie name for
     * @return non-conflicting proxy cookie name
     */
    protected String getProxyCookieName(HttpCookie cookie) {
        //
        return doPreserveCookies ? cookie.getName() : getCookieNamePrefix(cookie.getName()) + cookie.getName();
    }

    /**
     * Create path for proxy cookie.
     *
     * @param servletRequest original request
     * @return proxy cookie path
     */
    protected String buildProxyCookiePath(HttpServletRequest servletRequest) {
        String path = servletRequest.getContextPath(); // path starts with / or is empty string
        path += servletRequest.getServletPath(); // servlet path starts with / or is empty string
        if (path.isEmpty()) {
            path = "/";
        }
        return path;
    }

    /**
     * Take any client cookies that were originally from the proxy and prepare them to send to the
     * proxy.  This relies on cookie headers being set correctly according to RFC 6265 Sec 5.4.
     * This also blocks any local cookies from being sent to the proxy.
     */
    protected String getRealCookie(String cookieValue) {
        StringBuilder escapedCookie = new StringBuilder();
        String[] cookies = cookieValue.split("[;,]");
        for (String cookie : cookies) {
            String[] cookieSplit = cookie.split("=");
            if (cookieSplit.length == 2) {
                String cookieName = cookieSplit[0].trim();
                if (cookieName.startsWith(getCookieNamePrefix(cookieName))) {
                    cookieName = cookieName.substring(getCookieNamePrefix(cookieName).length());
                    if (escapedCookie.length() > 0) {
                        escapedCookie.append("; ");
                    }
                    escapedCookie.append(cookieName).append("=").append(cookieSplit[1].trim());
                }
            }
        }
        return escapedCookie.toString();
    }

    /**
     * The string prefixing rewritten cookies.
     */
    protected String getCookieNamePrefix(String name) {
        return "!Proxy!" + getServletConfig().getServletName();
    }


    private static ByteArrayOutputStream cloneInputStream(InputStream input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Copy response body data (the entity) from the proxy to the servlet client.
     */
    protected void copyResponseEntity(HttpResponse proxyResponse, HttpServletResponse servletResponse,
                                      HttpRequest proxyRequest, HttpServletRequest servletRequest, String accessId)
            throws IOException {
        HttpEntity entity = proxyResponse.getEntity();
        if (entity != null) {
            if (entity.isChunked()) {
                // Flush intermediate results before blocking on input -- needed for SSE
                InputStream is = entity.getContent();

                ByteArrayOutputStream baos = cloneInputStream(is);
                logger.info(this.getServletName() + " , " + accessId + " , proxy resp1: " + baos.toString("UTF-8"));
                InputStream stream1 = new ByteArrayInputStream(baos.toByteArray());

                OutputStream os = servletResponse.getOutputStream();
                byte[] buffer = new byte[10 * 1024];
                int read;
                while ((read = stream1.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                    /*-
                     * Issue in Apache http client/JDK: if the stream from client is
                     * compressed, apache http client will delegate to GzipInputStream.
                     * The #available implementation of InflaterInputStream (parent of
                     * GzipInputStream) return 1 until EOF is reached. This is not
                     * consistent with InputStream#available, which defines:
                     *
                     *   A single read or skip of this many bytes will not block,
                     *   but may read or skip fewer bytes.
                     *
                     *  To work around this, a flush is issued always if compression
                     *  is handled by apache http client
                     */
                    if (doHandleCompression || stream1.available() == 0 /* next is.read will block */) {
                        os.flush();
                    }
                }
                // Entity closing/cleanup is done in the caller (#service)
            } else {
                OutputStream servletOutputStream = servletResponse.getOutputStream();
                Header contentType = proxyResponse.getEntity().getContentType();
                String contentCharset = Utils.getContentCharset(contentType);
                String rawContent = getContentByWriteTo(entity, contentCharset);
                logger.info(this.getServletName() + " , " + accessId + " , proxy resp2: " + rawContent);
                List<Rule> bodyRule = Rule.getRule(ruleList, 3);
                for (Rule rule : bodyRule) {
                    if (!StringUtils.hasText(rule.getSource())) {
                        rawContent = rawContent.replace(rule.getSource(), rule.getContent());
                    } else {
                        rawContent = rule.getContent();
                    }
                }
                byte[] bytes = rawContent.getBytes(Charset.forName(contentCharset));
                final ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
                proxyResponse.setEntity(byteArrayEntity);

                proxyResponse.removeHeaders("Content-length");
                proxyResponse.addHeader("Content-length", "" + bytes.length);
                servletResponse.addHeader("Content-length", "" + bytes.length);

                String contentTypeStr = "application/json; charset=utf-8";
                if (contentType != null) {
                    contentTypeStr = contentType.getValue();
                    byteArrayEntity.setContentType(contentTypeStr);
                    proxyResponse.addHeader("Content-type", contentTypeStr);
                    proxyResponse.addHeader("Content-type", contentTypeStr);
                }

                byteArrayEntity.writeTo(servletOutputStream);

            }
        }
    }

    public String getContentByWriteTo(HttpEntity entity, String charset) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            entity.writeTo(outputStream);
            return outputStream.toString(charset);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Reads the request URI from {@code servletRequest} and rewrites it, considering targetUri.
     * It's used to make the new request.
     */
    protected String rewriteUrlFromRequest(HttpServletRequest servletRequest) {
        StringBuilder uri = new StringBuilder(500);
        uri.append(getTargetUri(servletRequest));
        // Handle the path given to the servlet
        String pathInfo = rewritePathInfoFromRequest(servletRequest);
        if (pathInfo != null) {//ex: /my/path.html
            // getPathInfo() returns decoded string, so we need encodeUriQuery to encode "%" characters
            uri.append(encodeUriQuery(pathInfo, true));
        }
        // Handle the query string & fragment
        String queryString = servletRequest.getQueryString();//ex:(following '?'): name=value&foo=bar#fragment
        String fragment = null;
        //split off fragment from queryString, updating queryString if found
        if (queryString != null) {
            int fragIdx = queryString.indexOf('#');
            if (fragIdx >= 0) {
                fragment = queryString.substring(fragIdx + 1);
                queryString = queryString.substring(0, fragIdx);
            }
        }

        queryString = rewriteQueryStringFromRequest(servletRequest, queryString);
        if (queryString != null && queryString.length() > 0) {
            uri.append('?');
            // queryString is not decoded, so we need encodeUriQuery not to encode "%" characters, to avoid double-encoding
            uri.append(encodeUriQuery(queryString, false));
        }

        if (doSendUrlFragment && fragment != null) {
            uri.append('#');
            // fragment is not decoded, so we need encodeUriQuery not to encode "%" characters, to avoid double-encoding
            uri.append(encodeUriQuery(fragment, false));
        }
        return uri.toString();
    }

    protected String rewriteQueryStringFromRequest(HttpServletRequest servletRequest, String queryString) {
        return queryString;
    }

    /**
     * Allow overrides of {@link HttpServletRequest#getPathInfo()}.
     * Useful when url-pattern of servlet-mapping (web.xml) requires manipulation.
     */
    protected String rewritePathInfoFromRequest(HttpServletRequest servletRequest) {
        return servletRequest.getPathInfo();
    }

    /**
     * For a redirect response from the target server, this translates {@code theUrl} to redirect to
     * and translates it to one the original client can use.
     */
    protected String rewriteUrlFromResponse(HttpServletRequest servletRequest, String theUrl) {
        //TODO document example paths
        final String targetUri = getTargetUri(servletRequest);
        if (theUrl.startsWith(targetUri)) {
            /*-
             * The URL points back to the back-end server.
             * Instead of returning it verbatim we replace the target path with our
             * source path in a way that should instruct the original client to
             * request the URL pointed through this Proxy.
             * We do this by taking the current request and rewriting the path part
             * using this servlet's absolute path and the path from the returned URL
             * after the base target URL.
             */
            StringBuffer curUrl = servletRequest.getRequestURL();//no query
            int pos;
            // Skip the protocol part
            if ((pos = curUrl.indexOf("://")) >= 0) {
                // Skip the authority part
                // + 3 to skip the separator between protocol and authority
                if ((pos = curUrl.indexOf("/", pos + 3)) >= 0) {
                    // Trim everything after the authority part.
                    curUrl.setLength(pos);
                }
            }
            // Context path starts with a / if it is not blank
            curUrl.append(servletRequest.getContextPath());
            // Servlet path starts with a / if it is not blank
            curUrl.append(servletRequest.getServletPath());
            curUrl.append(theUrl, targetUri.length(), theUrl.length());
            return curUrl.toString();
        }
        return theUrl;
    }

    /**
     * The target URI as configured. Not null.
     */
    public String getTargetUri() {
        return targetUri;
    }

    /**
     * Encodes characters in the query or fragment part of the URI.
     *
     * <p>Unfortunately, an incoming URI sometimes has characters disallowed by the spec.  HttpClient
     * insists that the outgoing proxied request has a valid URI because it uses Java's {@link URI}.
     * To be more forgiving, we must escape the problematic characters.  See the URI class for the
     * spec.
     *
     * @param in            example: name=value&amp;foo=bar#fragment
     * @param encodePercent determine whether percent characters need to be encoded
     */
    protected CharSequence encodeUriQuery(CharSequence in, boolean encodePercent) {
        //Note that I can't simply use URI.java to encode because it will escape pre-existing escaped things.
        StringBuilder outBuf = null;
        Formatter formatter = null;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            boolean escape = true;
            if (c < 128) {
                if (asciiQueryChars.get(c) && !(encodePercent && c == '%')) {
                    escape = false;
                }
            } else if (!Character.isISOControl(c) && !Character.isSpaceChar(c)) {//not-ascii
                escape = false;
            }
            if (!escape) {
                if (outBuf != null) {
                    outBuf.append(c);
                }
            } else {
                //escape
                if (outBuf == null) {
                    outBuf = new StringBuilder(in.length() + 5 * 3);
                    outBuf.append(in, 0, i);
                    formatter = new Formatter(outBuf);
                }
                //leading %, 0 padded, width 2, capital hex
                formatter.format("%%%02X", (int) c);//TODO
            }
        }
        return outBuf != null ? outBuf : in;
    }

    protected static final BitSet asciiQueryChars;

    static {
        char[] c_unreserved = "_-!.~'()*".toCharArray();//plus alphanum
        char[] c_punct = ",;:$&+=".toCharArray();
        char[] c_reserved = "/@".toCharArray();//plus punct.  Exclude '?'; RFC-2616 3.2.2. Exclude '[', ']'; https://www.ietf.org/rfc/rfc1738.txt, unsafe characters
        asciiQueryChars = new BitSet(128);
        for (char c = 'a'; c <= 'z'; c++) {
            asciiQueryChars.set(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            asciiQueryChars.set(c);
        }
        for (char c = '0'; c <= '9'; c++) {
            asciiQueryChars.set(c);
        }
        for (char c : c_unreserved) {
            asciiQueryChars.set(c);
        }
        for (char c : c_punct) {
            asciiQueryChars.set(c);
        }
        for (char c : c_reserved) {
            asciiQueryChars.set(c);
        }

        asciiQueryChars.set('%');//leave existing percent escapes in place
    }

}