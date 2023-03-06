package io.github.toohandsome.demo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
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

@RestController
@Slf4j
public class Tc1 {

    @GetMapping("/tt")
    public String tt() {
        HttpURLConnection con = null;

        BufferedReader buffer = null;
        StringBuffer resultBuffer = null;

        try {

            URL url1 = new URL("https://www.baidu.com");
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            URL url = new URL("http://www.baidu.com");
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


}
