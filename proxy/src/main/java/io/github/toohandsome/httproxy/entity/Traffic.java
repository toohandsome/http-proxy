package io.github.toohandsome.httproxy.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Traffic {
    private String key;
    private long cost;
    private long reqDate;
    private long respDate;
    private String direction;
    private String host;
    private String url;
    private int reqBodyLength;
    private int respBodyLength;
    private String method;
    private Map<String, String> requestHeaders= new HashMap<>();
    private Map<String, String> responseHeaders = new HashMap<>();
    private String requestBody;
    private String responseBody;
    private String stackTrace;
}
