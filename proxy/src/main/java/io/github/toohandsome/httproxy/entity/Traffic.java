package io.github.toohandsome.httproxy.entity;

import lombok.Data;

import java.util.Map;

@Data
public class Traffic {
    private String key;
    private long reqDate;
    private long respDate;
    private String direction;
    private String host;
    private String url;
    private int bodyLength;
    private String method;
    private Map<String, String> requestHeaders;
    private Map<String, String> responseHeaders;
    private String requestBody;
    private String responseBody;
}
