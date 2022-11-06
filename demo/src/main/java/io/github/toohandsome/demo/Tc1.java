package io.github.toohandsome.demo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
@Slf4j
public class Tc1 {


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
