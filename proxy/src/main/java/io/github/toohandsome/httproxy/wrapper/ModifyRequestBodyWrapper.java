///**
// * Copyright (C), 2015-2019
// */
//package io.github.toohandsome.httproxy.wrapper;
//
//import lombok.SneakyThrows;
//
//import javax.servlet.ReadListener;
//import javax.servlet.ServletInputStream;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequestWrapper;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * 修改http请求体和contentType后构建新的请求对象
// * 只针对请求体可读的请求类型
// *
// * @author zhaoxb
// * @create 2019-09-26 17:49
// */
//public class ModifyRequestBodyWrapper extends HttpServletRequestWrapper {
//    /**
//     * 原请求对象
//     */
//    private HttpServletRequest orginalRequest;
//
//    public HttpServletRequest getOrginalRequest() {
//        return orginalRequest;
//    }
//
//    public void setOrginalRequest(HttpServletRequest orginalRequest) {
//        this.orginalRequest = orginalRequest;
//    }
//
//    /**
//     * 修改后的请求体
//     */
//    private String modifyRequestBody;
//
//    private Map<String, String> headerMap = new HashMap<String, String>();
//    private List <String> delHeaderList = new ArrayList<>();
//
//    /**
//     * add a header with given name and value
//     *
//     * @param name
//     * @param value
//     */
//    public void addHeader(String name, String value) {
//        headerMap.put(name, value);
//    }
//
//    public void removeHeader(String name) {
//        delHeaderList.add(name);
//    }
//
//    @Override
//    public String getHeader(String name) {
//        if (delHeaderList.contains(name)) {
//            return null;
//        }
//        String headerValue = super.getHeader(name);
//        if (headerMap.containsKey(name)) {
//            headerValue = headerMap.get(name);
//        }
//        return headerValue;
//    }
//
//    /**
//     * get the Header names
//     */
//    @Override
//    public Enumeration<String> getHeaderNames() {
//
//        List<String> names = Collections.list(super.getHeaderNames());
//        for (String name : headerMap.keySet()) {
//            names.add(name);
//        }
//
//        List<String> collect = names.stream().filter(item -> !delHeaderList.contains(item)).collect(Collectors.toList());
//
//        return Collections.enumeration(collect);
//    }
//
//
//    @Override
//    public Enumeration<String> getHeaders(String name) {
//
//        if (delHeaderList.contains(name)) {
//            return Collections.<String>emptyEnumeration();
//        }
//        List<String> values = Collections.list(super.getHeaders(name));
//        if (headerMap.containsKey(name)) {
//            values.add(headerMap.get(name));
//        }
//        return Collections.enumeration(values);
//
//    }
//
//
//    /**
//     * 修改请求体，请求类型沿用原来的
//     *
//     * @param orginalRequest    原请求对象
//     * @param modifyRequestBody 修改后的请求体
//     */
//    public ModifyRequestBodyWrapper(HttpServletRequest orginalRequest, String modifyRequestBody) {
//        super(orginalRequest);
//        this.modifyRequestBody = modifyRequestBody;
//        this.orginalRequest = orginalRequest;
//    }
//
//
//    /**
//     * 构建新的输入流，在新的输入流中放入修改后的请求体（使用原请求中的字符集）
//     *
//     * @return 新的输入流（包含修改后的请求体）
//     */
//    @Override
//    @SneakyThrows
//    public ServletInputStream getInputStream() {
//        return new ServletInputStream() {
//            private InputStream in = new ByteArrayInputStream(modifyRequestBody.getBytes(orginalRequest.getCharacterEncoding()));
//
//            @Override
//            public int read() throws IOException {
//                return in.read();
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public boolean isReady() {
//                return false;
//            }
//
//            @Override
//            public void setReadListener(ReadListener readListener) {
//
//            }
//        };
//    }
//
//    /**
//     * 获取新的请求体大小
//     *
//     * @return
//     */
//    @Override
//    @SneakyThrows
//    public int getContentLength() {
//        return modifyRequestBody.getBytes(orginalRequest.getCharacterEncoding()).length;
//    }
//
//    /**
//     * 获取新的请求体大小
//     *
//     * @return
//     */
//    @Override
//    @SneakyThrows
//    public long getContentLengthLong() {
//        return modifyRequestBody.getBytes(orginalRequest.getCharacterEncoding()).length;
//    }
//
//}