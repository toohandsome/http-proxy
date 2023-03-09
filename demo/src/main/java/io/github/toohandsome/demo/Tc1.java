package io.github.toohandsome.demo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

import java.io.File;

import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class Tc1 {

    @GetMapping("/tt")
    public String tt() {
        HttpURLConnection con = null;

        BufferedReader buffer = null;
        StringBuffer resultBuffer = null;

        try {


            URL url = new URL("http://www.zuanke8.com/");
            //得到连接对象
            con = (HttpURLConnection) url.openConnection();
            //设置请求类型
            con.setRequestMethod("POST");
            //设置Content-Type，此处根据实际情况确定
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //允许写出
            con.setDoOutput(true);
            //允许读入
            con.setDoInput(true);
            //不使用缓存
            con.setUseCaches(false);
            OutputStream os = con.getOutputStream();
            Map paraMap = new HashMap();
            paraMap.put("type", "wx");
            paraMap.put("mchid", "10101");
            //组装入参
            os.write(("consumerAppId=test&serviceName=queryMerchantService&params=" + JSON.toJSONString(paraMap)).getBytes());
            //得到响应码
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //得到响应流
                InputStream inputStream = con.getInputStream();
                //sun.net.www.http.HttpClient.getInputStream
                //将响应流转换成字符串
                resultBuffer = new StringBuffer();
                String line;
                buffer = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
                while ((line = buffer.readLine()) != null) {
                    resultBuffer.append(line);
                }
                System.out.println("result:" + resultBuffer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "t1";
    }

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/tt2")
    public String tt2() throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://127.0.0.1:8080/routeView/index.html");
        //使用代理服务器
//        HttpHost httpHost = new HttpHost("127.0.0.1", 9999);
        RequestConfig config = RequestConfig.custom()
//                .setProxy(httpHost)
                .build();
        httpGet.setConfig(config);
        CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        //输出网页内容
        System.out.println("网页内容:");
        System.out.println(EntityUtils.toString(entity, "utf-8"));
        response.close();
        return "";
    }

    @GetMapping("/tt1")
    public String tt1() throws Exception {
//        final ResponseEntity<String> forEntity = restTemplate.getForEntity("http://127.0.0.1/t1", String.class, new HashMap<>());
//        System.out.println("forEntity: " + forEntity.getBody());

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://www.piaohua.com/")
                .build();
        try {
            Response response = client.newCall(request).execute();
            response.close();
            if (response.isSuccessful())
                System.out.println("成功");

        } catch ( IOException e) {
            e.printStackTrace();
        }

        tt();
        tt2();
        final ResponseEntity<String> forEntity = restTemplate.getForEntity("http://help.locoy.com/", String.class);
        final String body = forEntity.getBody();
        System.out.println(body);

//        String body = HttpUtil.createGet("http://www.baidu.com").execute().body();
//        System.out.println(body);
        String result1= HttpUtil.get("https://www.baidu.com");
        System.out.println(result1);
//        CloseableHttpClient httpClient1 = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet("http://127.0.0.1/t1");
//        CloseableHttpResponse response = httpClient1.execute(httpGet);
//        HttpEntity entity = response.getEntity();
//        String ret = EntityUtils.toString(entity, "UTF-8");
//        System.out.println(ret);


//        for (int i = 0; i < 4; i++) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (true) {
//                        try {
//                            CloseableHttpClient httpClient1 = HttpClients.createDefault();
//                            HttpGet httpPost = new HttpGet("http://127.0.0.1:8080/t1");
//                            CloseableHttpResponse response = httpClient1.execute(httpPost);
//                            HttpEntity entity = response.getEntity();
//                            String ret = EntityUtils.toString(entity, "UTF-8");
//                            System.out.println(ret);
//                        } catch (Exception e) {
//                            System.out.println("1");
//                        }
//                    }
//                }
//            }).start();
//        }

        return "t1";
    }


    @GetMapping("/t1")
    public String t1(HttpServletRequest request, Integer times) {
        logger.info("times: " + times);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }

        return "t1";
    }

    @GetMapping("/t2")
    public String t2(HttpServletRequest request, String a) {
        logger.info("t2 : a " + a);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        return "t2";
    }


    @PostMapping("/t3")
    public User t3(HttpServletRequest request, @RequestBody User user) {
        logger.info("t3: user: " + JSON.toJSON(user));
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        return user;
    }

    @PostMapping("/t4")
    public String t4(HttpServletRequest request, @RequestBody User user) {
        logger.info("t4: user: " + JSON.toJSON(user));
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 20000; i++) {
            sb.append("a");
        }
        return sb.toString();
    }


    @PostMapping("/t5")
    public String t5(HttpServletRequest request, int i, String b) {
        logger.info("t5 : i " + i + " , b: " + b);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        return (i + b);
    }

    @GetMapping("/t6")
    public String t6(HttpServletRequest request, String a) {
        logger.info("t6 : a " + a);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 20000; i++) {
            sb.append("a");
        }
        return sb.toString();
    }


    @GetMapping("/t7")
    public String t7(String packageName, String level) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = context.getLogger(packageName);

        logger.setLevel(Level.toLevel(level));
        // Don't inherit root appender
        logger.setAdditive(false);

        ConsoleAppender consoleAppender = new ConsoleAppender();

        RollingFileAppender rollingFile = new RollingFileAppender();
        rollingFile.setContext(context);
        rollingFile.setName("dynamic_logger_fileAppender");

        // Optional
        rollingFile.setFile("/log"
                + File.separator + "msg.log");
        rollingFile.setAppend(true);

        // Set up rolling policy
        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
        rollingPolicy.setFileNamePattern("/log"
                + File.separator + "%d{yyyy-MM,aux}"
                + File.separator + "msg_%d{yyyy-MM-dd_HH-mm}.txt");

        rollingPolicy.setParent(rollingFile);
        rollingPolicy.setContext(context);
        rollingPolicy.start();

        // set up pattern encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%-4relative [%thread] %-5level %logger{35} - %msg%n");
        encoder.start();

        rollingFile.setRollingPolicy(rollingPolicy);
        rollingFile.setEncoder(encoder);
        rollingFile.start();

        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        // Atach appender to logger
        logger.addAppender(rollingFile);
        logger.addAppender(consoleAppender);
        return "";
    }

    @GetMapping("/t8")
    public String t8(String packageName, String level) {
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        logger.debug("debug");

        return "";
    }

    public static void main(String[] args) {
        String messageid = UUID.randomUUID().toString().replace("-", "");
        System.out.println(messageid.length());
    }

    @GetMapping("/t9")
    public String t9() throws IOException {
        String url = "https://www.stgl-sirius.com/ydqmjk/api/1.0.1/medical/scanLogin";
        final String s = sendCaReq(url, "e30=");
        return s;
    }

    @GetMapping("/t10")
    public String t10(String transactionId) throws IOException {
        String url = "https://www.stgl-sirius.com/ydqmjk/api/1.0.1/medical/authState";
        final String message = "{\"transactionId\":\"" + transactionId + "\"}";
        final String s1 = new String(Base64.getEncoder().encode(message.getBytes(StandardCharsets.UTF_8)));
        final String s = sendCaReq(url, s1);
        return s;
    }

    CloseableHttpClient httpClient = HttpClients.createDefault();

    public String sendCaReq(String url, String message) throws IOException {
        String appKey = "2cf7a341657a87a3dd110a0049c96651";
        String appid = "18b19fc77e57497087c7c0b6b8188534";
        String datetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String format = "JSON";

        String messageid = UUID.randomUUID().toString().replace("-", "");
        String version = "1.0.1";

        StringBuilder sb = new StringBuilder(url).append("?");
        StringBuilder sb1 = new StringBuilder();
        sb.append("appid").append("=").append(appid).append("&");
        sb.append("datetime").append("=").append(datetime).append("&");
        sb.append("format").append("=").append(format).append("&");
        sb.append("message").append("=").append(message).append("&");
        sb.append("messageid").append("=").append(messageid).append("&");
        sb.append("version").append("=").append(version).append("&");
        final String secStr = sb1.append(appKey).append(appid).append(datetime).append(format).append(message).append(messageid).append(version).append(appKey).toString();

        String security = DigestUtils.md5Hex(secStr).toUpperCase();
        System.out.println("5.4 md5  ： " + security);
        sb.append("security").append("=").append(security);

        HttpPost httpPost = new HttpPost(sb.toString());
        CloseableHttpResponse response = httpClient.execute(httpPost);

        String ret = "";
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();

            // 获取返回的信息
            ret = EntityUtils.toString(entity, "UTF-8");
            System.out.println(ret);
        } else {
            System.out.println("删除失败，请重试！！！");
        }

        // 关闭response、HttpClient资源
        response.close();
//        httpClient.close();
        return ret;
    }


}
