package demo.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import demo.entity.User;
import demo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;

@RestController
@Slf4j
public class Tc1 {


//    @Autowired
//    RestTemplate restTemplate;

    @GetMapping("/tt")
    public String tt() throws Exception {
        final ResponseEntity<String> forEntity = new RestTemplate().getForEntity("https://www.baidu.com", String.class, new HashMap<>());
        System.out.println("forEntity: " + forEntity.getBody());
        return "t1";
    }

    @GetMapping("/tomcatPoll")
    public String tomcatPoll() throws Exception {
        return SpringUtil.getTomcatE().toJSONString(JSONWriter.Feature.PrettyFormat);
    }

    public String tomcatPoll1() throws Exception {
        return SpringUtil.getTomcatE().toJSONString(JSONWriter.Feature.PrettyFormat);
    }


    @GetMapping("/t1")
    public String t1(HttpServletRequest request, Integer times) throws Exception {

        logger.info("times: " + times);
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            System.out.println(headerName + ": " + request.getHeader(headerName));
//        }
        Thread.sleep(1000);

        return "t1";
    }

    @PostMapping("/t2")
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
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            System.out.println(headerName + ": " + request.getHeader(headerName));
//        }
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


}
