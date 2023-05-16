/**
 * Copyright (C), 2015-2019
 */
package io.github.toohandsome.httproxy.wrapper;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.ResponseFacade;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * 构建新的响应对象，缓存响应体
 * 可以通过此对象获取响应体，然后进行修改，通过原响应流返回给调用方
 *
 * @author zhaoxb
 * @create 2019-09-26 17:52
 */
/**
 * @author toohandsome
 */
public class ModifyResponseBodyWrapper extends ResponseFacade {


    @Override
    public Collection<String> getHeaderNames() {
        return super.getHeaderNames();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return super.getHeaders(name);
    }

    @Override
    public String getHeader(String name) {
        return super.getHeader(name);
    }

    public ModifyResponseBodyWrapper(Response resp) {
        super(resp);

    }



}