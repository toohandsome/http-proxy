//package io.github.toohandsome.httproxy.util;
//
//import com.alibaba.fastjson2.JSON;
//import io.github.toohandsome.httproxy.entity.Traffic;
//
//import java.io.*;
//import java.net.Socket;
//import java.util.Date;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class TestClient {
//    public static void main(String[] args) throws IOException {
//
//
//        Traffic traffic = new Traffic();
//        traffic.setKey("123");
//        traffic.setDirection("up");
//        traffic.setHost("www.baidu.com");
//        traffic.setMethod("get");
//        traffic.setReqDate(123456789);
//        traffic.setRespDate(1531515656);
//        traffic.setReqBodyLength(1212);
//        traffic.setRespBodyLength(4444);
//        traffic.setUrl("/dasiuhdasuid");
//        traffic.setRequestBody("请求体");
//        traffic.setResponseBody("响应体");
//        System.out.println(JSON.toJSONString(traffic));
//
//
//        String json = " {\"respDate\":1678810627036,\"reqBodyLength\":19825,\"direction\":\"down\",\"responseBody\":\"<!DOCTYPE html>\\r\\n<!--STATUS OK-->\\r\\n<html>\\r\\n<head>\\r\\n    <meta http-equiv=\\\"X-UA-Compatible\\\" content=\\\"IE=edge,chrome=1\\\">\\r\\n    <meta http-equiv=\\\"content-type\\\" content=\\\"text/html;charset=utf-8\\\">\\r\\n    <meta content=\\\"always\\\" name=\\\"referrer\\\">\\r\\n    <script src=\\\"https://ss1.bdstatic.com/5eN1bjq8AAUYm2zgoY3K/r/www/nocache/imgdata/seErrorRec.js\\\"></script>\\r\\n    <title>页面不存在_百度搜索</title>\\r\\n    <style data-for=\\\"result\\\">\\r\\n        body {color: #333; background: #fff; padding: 0; margin: 0; position: relative; min-width: 700px; font-family: Arial, 'Microsoft YaHei'; font-size: 12px }\\r\\n        p, form, ol, ul, li, dl, dt, dd, h3 {margin: 0; padding: 0; list-style: none }\\r\\n        input {padding-top: 0; padding-bottom: 0; -moz-box-sizing: border-box; -webkit-box-sizing: border-box; box-sizing: border-box } img {border: none; }\\r\\n        .logo {width: 117px; height: 38px; cursor: pointer }\\r\\n         #wrapper {_zoom: 1 }\\r\\n        #head {padding-left: 35px; margin-bottom: 20px; width: 900px }\\r\\n        .fm {clear: both; position: relative; z-index: 297 }\\r\\n        .btn, #more {font-size: 14px } \\r\\n        .s_btn {width: 95px; height: 32px; padding-top: 2px\\9; font-size: 14px; \",\"requestHeaders\":{},\"responseHeaders\":{\"null\":\"HTTP/1.1 200 OK\",\"Server\":\"Apache\",\"Last-Modified\":\"Wed, 10 Mar 2021 06:27:44 GMT\",\"P3p\":\"CP=\\\" OTI DSP COR IVA OUR IND COM \\\"\",\"Date\":\"Tue, 14 Mar 2023 16:17:08 GMT\",\"Accept-Ranges\":\"bytes\",\"Cache-Control\":\"max-age=86400\",\"Etag\":\"\\\"4d71-5bd28c3bf7800\\\"\",\"Set-Cookie\":\"BAIDUID=0DAA2C6364839174548CB80CE26CBCE1:FG=1; expires=Wed, 13-Mar-24 16:17:08 GMT; max-age=31536000; path=/; domain=.baidu.com; version=1\",\"Vary\":\"Accept-Encoding,User-Agent\",\"Expires\":\"Wed, 15 Mar 2023 16:17:08 GMT\",\"Content-Length\":\"19825\",\"Content-Type\":\"text/html\"},\"key\":\"1673660723\"}";
//
//        Traffic parse = JSON.parseObject(json, Traffic.class);
//
//        System.out.println(new Date());
//        AtomicInteger ii = new AtomicInteger(0);
//        for (int j = 0; j < 100; j++) {
//            new Thread(() -> {
//                try {
//                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new Socket("localhost", 8009).getOutputStream()));
//                    for (int i = 0; i < 20000; i++) {
//                        int andIncrement = ii.getAndIncrement();
//                        System.out.println(andIncrement);
//                        writer.write(String.valueOf(andIncrement+"中文测试 联调 蓝瞳  联通a联通aaaaaaaaa联通a联通aaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhh联通a联通aaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhh联通a联通aaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhh联通a联通aaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhhaaaaaaaaaaaaaaa88888888888888888888888222222222222222222222hhhhhhhhhhh"+i+"$_httpProxy_$"));
//                        writer.flush();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }
//
//    }
//}