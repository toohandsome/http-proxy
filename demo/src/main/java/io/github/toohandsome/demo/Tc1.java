package io.github.toohandsome.demo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import cn.hutool.http.HttpUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.toohandsome.demo.config.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;
import org.apache.commons.codec.CharEncoding;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

import java.util.zip.GZIPInputStream;

import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import sun.net.www.http.ChunkedInputStream;
import sun.net.www.protocol.https.HttpsURLConnectionImpl;

@RestController
@Slf4j
public class Tc1 {

    @Value("${a:aaa}")
    private String a ;
    @GetMapping("/a")
    public String a() {
        return a;
    }

    @GetMapping("/tt")
    public String tt() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://www.baidu.com");
//            URL url = new URL("http://127.0.0.1/t8");
            connection = (HttpURLConnection) url.openConnection();
            //通过此方法创建的HttpURLConnection对象，并没有真正执行连接操作，只是创建了一个新的实例，在正式连接前，往往还需要设置一些属性，如连接超时和请求方式等
            connection.setRequestMethod("POST"); //设置请求方式
            connection.setConnectTimeout(8000);//设置连接超时时间
            connection.setReadTimeout(8000);//设置读取超时时间
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Accept-Language", "max-age=0");
            connection.setRequestProperty("Connection", "keep-alive");
//            connection.setRequestProperty("Cookie", "BIDUPSID=76A98B766DD25BE686350B0D000BEE53; PSTM=1661787931; BAIDUID=76A98B766DD25BE686350B0D000BEE53:SL=0:NR=10:FG=1; MCITY=-75%3A; BDUSS=ltajg4cVNycHZKcmdLMzF1WGo0ZzBOSkxyaHNBWFRINlVob356N3RsRGU3NmRqSVFBQUFBJCQAAAAAAAAAAAEAAACwPBIYz8TStrLdAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN5igGPeYoBjVX; BDUSS_BFESS=ltajg4cVNycHZKcmdLMzF1WGo0ZzBOSkxyaHNBWFRINlVob356N3RsRGU3NmRqSVFBQUFBJCQAAAAAAAAAAAEAAACwPBIYz8TStrLdAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN5igGPeYoBjVX; BD_UPN=12314753; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; BD_HOME=1; delPer=0; BD_CK_SAM=1; BAIDUID_BFESS=76A98B766DD25BE686350B0D000BEE53:SL=0:NR=10:FG=1; BA_HECTOR=81858h84ak04ak0g840101991i0p3tg1n; ZFY=kUFN3X8c6l5IEOwq8GSflhCg9O:B95feKsZlRj0fRNeE:C; channel=baidusearch; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; PSINO=6; sug=3; sugstore=0; ORIGIN=2; bdime=0; B64_BOT=1; ab_sr=1.0.1_NDZmNzJlMWI1ZWExMzgzOTY0NTEzM2I2NDgxNWUwY2Y4ZGZhZDNiOWY0ZmNjYTliM2UyOWFlYWVhYTRlM2E0MTg1ZmIyZjJmZWYyMjE3ZDVmNTM5NGYzMzk2ZjE3ODQ3YzM1NmFkYzk1MmFhYmJkODEwYTQ3ZjFhM2E0MTNiNmFiZWJhNjIxZDQ0ZTRlOTZkYjk2YTI4Y2FhYTBhNDQxY2Y2NDk2YWRlOTViMGJmZDgzNjgyNjQ0MDY4ZGE0OGI3; RT=\"z=1&dm=baidu.com&si=5f6451a5-172d-4749-9a02-ea3141f12300&ss=lf46hzd5&sl=2&tt=1no&bcn=https%3A%2F%2Ffclog.baidu.com%2Flog%2Fweirwood%3Ftype%3Dperf&ld=61f\"; H_PS_PSSID=38185_36550_37513_37862_38174_38290_38254_36803_37936_38315_26350_37957_38283_37881; H_PS_645EC=59614vW%2BR0AUnPTbDvzFlSR3gGA07JHWnLYJNGCowbnjSHmwOo2SFp8eS90; baikeVisitId=0ca9639e-656f-4074-9378-80367e69cc26; BDSVRTM=0");
            connection.setRequestProperty("DNT", "1");
            connection.setRequestProperty("Host", "www.baidu.com");
            connection.setRequestProperty("asec-ch-ua", "\"Chromium\";v=\"110\", \"Not A(Brand\";v=\"24\", \"Microsoft Edge\";v=\"110\"");
            connection.setRequestProperty("bsec-ch-ua-mobile", "?0");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.63");
            connection.setRequestProperty("cSec-Fetch-Mode", "navigate");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setDoOutput(true);
            //允许读入
            connection.setDoInput(true);
            connection.connect();//连接远程资源
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            JSONObject obj = new JSONObject();
            obj.put("name", "yxd");
            obj.put("big", true);
            obj.put("address", "xxxx");
            obj.put("age", 232);
            out.writeBytes(obj.toJSONString());
            out.flush();
            out.close();
            InputStream input = connection.getInputStream();//获取到服务器返回的响应流
//            input = new GZIPInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "utf-8"));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect(); //关闭该http
        }

        return "t1";
    }

    @Autowired
    RestTemplate restTemplate1;

    @Autowired
    RestTemplate restTemplate2;

    @GetMapping("/tt2")
    public String tt2() throws Exception {

        HttpPost httpPost = new HttpPost("https://www.baidu.com");
        JSONObject json = new JSONObject();
        json.put("name", "yxd");
        json.put("big", true);
        json.put("address", "xxxx");
        json.put("age", 232);
        httpPost.addHeader("Content-type", "application/json;charset=UTF-8");
        httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        httpPost.addHeader("Accept-Encoding", "gzip, deflate, br");
        httpPost.addHeader("Accept-Language", "max-age=0");
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.setEntity(new StringEntity(json.toString(), "UTF-8"));
        for (int i = 0; i < 5; i++) {
            final CloseableHttpResponse response = HttpUtils.getHttpClient().execute(httpPost);
            HttpEntity entity = response.getEntity();
            //
            System.out.println("输出网页内容: " + EntityUtils.toString(entity, "utf-8"));
            response.close();
        }

        return "";
    }

    @GetMapping("/tt1")
    public String tt1() throws Exception {
//        final ResponseEntity<String> forEntity = restTemplate.getForEntity("http://127.0.0.1/t1", String.class, new HashMap<>());
//        System.out.println("forEntity: " + forEntity.getBody());
        JSONObject json = new JSONObject();
        json.put("name", "yxd");
        json.put("big", true);
        json.put("address", "xxxx");
        json.put("age", 232);
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");

        okhttp3.RequestBody  requestBody = okhttp3.RequestBody.create(JSON,json.toJSONString());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
//                .url("https://www.baidu.com/")
                .url("http://127.0.0.1/t8")
                .post(requestBody)
                .build();
        try {

            Headers headers = request.headers();
            Set<String> names = headers.names();
            for (int i = 0; i < names.size(); i++) {

            }

            Response response = client.newCall(request).execute();

            ResponseBody body = response.body();

            String string = body.string();
            System.out.println(string);
            response.close();
            if (response.isSuccessful()) {
                System.out.println("poaohua 完成");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//
//        tt();
//        System.out.println("zuanke8 完成");
//        tt2();
//        System.out.println("127 完成");
//        final ResponseEntity<String> forEntity = restTemplate1.getForEntity("http://help.locoy.com/", String.class);
//        final String body = forEntity.getBody();
//        System.out.println("locoy 完成");

//        String body = HttpUtil.createGet("http://www.baidu.com").execute().body();
//        System.out.println(body);
//        String result1 = HttpUtil.get("https://www.baidu.com");
//        System.out.println("baidu 完成");

        return "t1";
    }

    public static final Proxy PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 9658));

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
    public String t2(String a) {

        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.proxy(PROXY);
            OkHttpClient client = builder.build();
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://www.piaohua.com/");
            client.newCall(requestBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("piaohua 完成");
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("http://127.0.0.1:8080/httpProxy/index.html");
            //使用代理服务器
            HttpHost httpHost = new HttpHost("127.0.0.1", 9658);
            RequestConfig config = RequestConfig.custom()
                    .setProxy(httpHost)
                    .build();
            httpGet.setConfig(config);
            CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            //输出网页内容
//            System.out.println("网页内容:");
//            System.out.println(EntityUtils.toString(entity, "utf-8"));
            response.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("127 完成");
        try {

            HttpURLConnection con = null;

            BufferedReader buffer = null;
            StringBuffer resultBuffer = null;
            URL url = new URL("http://blog.csdn.net/");
            //得到连接对象
            con = (HttpURLConnection) url.openConnection(PROXY);
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
            Map paraMap = new HashMap(10);
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
//                System.out.println("result:" + resultBuffer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("zuanke8 完成");
        try {
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setProxy(
                    PROXY
            );
            restTemplate1.setRequestFactory(requestFactory);
            final ResponseEntity<String> forEntity = restTemplate1.getForEntity("http://help.locoy.com/", String.class);
            final String body = forEntity.getBody();
            System.out.println("locoy 完成");
        } catch (Exception E) {
            E.printStackTrace();
        }

        return "t2";
    }


    @GetMapping("/t3")
    public User t3() {

        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.proxy(PROXY);
            OkHttpClient client = builder.build();
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://www.piaohua.com/");
            client.newCall(requestBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("piaohua 完成");

        try {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("https://www.baidu.com");
                connection = (HttpURLConnection) url.openConnection();
                //通过此方法创建的HttpURLConnection对象，并没有真正执行连接操作，只是创建了一个新的实例，在正式连接前，往往还需要设置一些属性，如连接超时和请求方式等
                connection.setRequestMethod("GET"); //设置请求方式
                connection.setConnectTimeout(8000);//设置连接超时时间
                connection.setReadTimeout(8000);//设置读取超时时间
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
                connection.setRequestProperty("Accept-Language", "max-age=0");
                connection.setRequestProperty("Connection", "keep-alive");
                connection.setRequestProperty("Cookie", "BIDUPSID=76A98B766DD25BE686350B0D000BEE53; PSTM=1661787931; BAIDUID=76A98B766DD25BE686350B0D000BEE53:SL=0:NR=10:FG=1; MCITY=-75%3A; BDUSS=ltajg4cVNycHZKcmdLMzF1WGo0ZzBOSkxyaHNBWFRINlVob356N3RsRGU3NmRqSVFBQUFBJCQAAAAAAAAAAAEAAACwPBIYz8TStrLdAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN5igGPeYoBjVX; BDUSS_BFESS=ltajg4cVNycHZKcmdLMzF1WGo0ZzBOSkxyaHNBWFRINlVob356N3RsRGU3NmRqSVFBQUFBJCQAAAAAAAAAAAEAAACwPBIYz8TStrLdAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAN5igGPeYoBjVX; BD_UPN=12314753; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; BD_HOME=1; delPer=0; BD_CK_SAM=1; BAIDUID_BFESS=76A98B766DD25BE686350B0D000BEE53:SL=0:NR=10:FG=1; BA_HECTOR=81858h84ak04ak0g840101991i0p3tg1n; ZFY=kUFN3X8c6l5IEOwq8GSflhCg9O:B95feKsZlRj0fRNeE:C; channel=baidusearch; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; PSINO=6; sug=3; sugstore=0; ORIGIN=2; bdime=0; B64_BOT=1; ab_sr=1.0.1_NDZmNzJlMWI1ZWExMzgzOTY0NTEzM2I2NDgxNWUwY2Y4ZGZhZDNiOWY0ZmNjYTliM2UyOWFlYWVhYTRlM2E0MTg1ZmIyZjJmZWYyMjE3ZDVmNTM5NGYzMzk2ZjE3ODQ3YzM1NmFkYzk1MmFhYmJkODEwYTQ3ZjFhM2E0MTNiNmFiZWJhNjIxZDQ0ZTRlOTZkYjk2YTI4Y2FhYTBhNDQxY2Y2NDk2YWRlOTViMGJmZDgzNjgyNjQ0MDY4ZGE0OGI3; RT=\"z=1&dm=baidu.com&si=5f6451a5-172d-4749-9a02-ea3141f12300&ss=lf46hzd5&sl=2&tt=1no&bcn=https%3A%2F%2Ffclog.baidu.com%2Flog%2Fweirwood%3Ftype%3Dperf&ld=61f\"; H_PS_PSSID=38185_36550_37513_37862_38174_38290_38254_36803_37936_38315_26350_37957_38283_37881; H_PS_645EC=59614vW%2BR0AUnPTbDvzFlSR3gGA07JHWnLYJNGCowbnjSHmwOo2SFp8eS90; baikeVisitId=0ca9639e-656f-4074-9378-80367e69cc26; BDSVRTM=0");
                connection.setRequestProperty("DNT", "1");
                connection.setRequestProperty("Host", "www.baidu.com");
                connection.setRequestProperty("sec-ch-ua", "\"Chromium\";v=\"110\", \"Not A(Brand\";v=\"24\", \"Microsoft Edge\";v=\"110\"");
                connection.setRequestProperty("sec-ch-ua-mobile", "?0");
                connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.63");
                connection.setRequestProperty("Sec-Fetch-Mode", "navigate");

                connection.connect();//连接远程资源

                InputStream input = connection.getInputStream();//获取到服务器返回的响应流
                InputStream stream = new GZIPInputStream(input);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
//                System.out.println(sb.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect(); //关闭该http
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("baidu完成");
        try {

            HttpURLConnection con = null;

            BufferedReader buffer = null;
            StringBuffer resultBuffer = null;
            URL url = new URL("http://blog.csdn.net/");
            //得到连接对象
            con = (HttpURLConnection) url.openConnection();
//            con = (HttpURLConnection) url.openConnection(PROXY);
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
            Map paraMap = new HashMap(10);
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
//                System.out.println("result:" + resultBuffer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("zuanke8完成");

        try {
//            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//            requestFactory.setProxy(
//                    PROXY
//            );
//            restTemplate.setRequestFactory(requestFactory);
            final ResponseEntity<String> forEntity = restTemplate2.getForEntity("http://help.locoy.com/", String.class);
            final String body = forEntity.getBody();
//            System.out.println(body);
        } catch (Exception E) {
            E.printStackTrace();
        }
        System.out.println("locoy完成");

        return null;
    }

    @GetMapping("/reset")
    public String reset(HttpServletRequest request) throws Exception {

        //创建发送端Socket对象（创建连接）
        Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 10086);
        //获取输出流对象
        OutputStream os = socket.getOutputStream();
        //发送数据
        String str = "Hi,TCP!";
        os.write(str.getBytes());
        //释放资源
        //os.close();
        socket.close();


        return "";
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

    @PostMapping("/t8")
    public String t8(@RequestBody User user) {
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        logger.debug(JSON.toJSONString(user));

        return "aaaaaaaaaa";
    }

    public static void main(String[] args) throws Exception {
//        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(MediaType.parse("text/plain"), "Hello, world!");
//
//        ByteArrayOutputStream byteArrayOutputStream123 = new ByteArrayOutputStream();
//        BufferedSink bufferedSink = Okio.buffer(Okio.sink(byteArrayOutputStream123));
//        requestBody.writeTo(bufferedSink);
//        bufferedSink.flush();
//        bufferedSink.close();
//
//        byte[] bytes = byteArrayOutputStream123.toByteArray();
//        String respBody = new String(bytes, StandardCharsets.UTF_8);
//        System.out.println("respBody: " + respBody);

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
