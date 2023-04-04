package io.github.toohandsome.attach.entity;

import java.util.Map;

public class Traffic extends AgentEntity {
    private String key;
    private long reqDate;
    private long respDate;
    private String direction;
    private String host;
    private String from;
    private String url;
    private long reqBodyLength;
    private long respBodyLength;
    private String method;
    private MyMap requestHeaders = new MyMap();
    private MyMap responseHeaders = new MyMap();
    private String requestBody;
    private String responseBody;
    private String stackTrace;

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("{");
        if (reqDate != 0) {
            stringBuilder.append("\"reqDate\":");
            stringBuilder.append(reqDate);
            stringBuilder.append(",");
        }
        if (respDate != 0) {
            stringBuilder.append("\"respDate\":");
            stringBuilder.append(respDate);
            stringBuilder.append(",");
        }
        if (reqBodyLength != 0) {
            stringBuilder.append("\"reqBodyLength\":");
            stringBuilder.append(reqBodyLength);
            stringBuilder.append(",");
        }
        if (respBodyLength != 0) {
            stringBuilder.append("\"respBodyLength\":");
            stringBuilder.append(respBodyLength);
            stringBuilder.append(",");
        }

        stringBuilder.append("\"direction\":\"");
        stringBuilder.append(direction);
        stringBuilder.append("\",");
        if (host != null) {
            stringBuilder.append("\"host\":\"");
            stringBuilder.append(host.replace("\"", "\\\""));
            stringBuilder.append("\",");
        }
        if (url != null) {
            stringBuilder.append("\"url\":\"");
            stringBuilder.append(url);
            stringBuilder.append("\",");
        }
        if (method != null) {
            stringBuilder.append("\"method\":\"");
            stringBuilder.append(method);
            stringBuilder.append("\",");
        }
        if (from != null) {
            stringBuilder.append("\"from\":\"");
            stringBuilder.append(from);
            stringBuilder.append("\",");
        }
        if (requestBody != null) {
            stringBuilder.append("\"requestBody\":\"");
            stringBuilder.append(requestBody.replace("\\", "\\\\").replace("\"", "\\\"").replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
            stringBuilder.append("\",");
        }
        if (responseBody != null) {
            stringBuilder.append("\"responseBody\":\"");
            stringBuilder.append(responseBody.replace("\\", "\\\\").replace("\"", "\\\"").replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
            stringBuilder.append("\",");
        }
        if (stackTrace != null) {
            stringBuilder.append("\"stackTrace\":\"");
            stringBuilder.append(stackTrace.replace("\\", "\\\\").replace("\"", "\\\"").replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
            stringBuilder.append("\",");
        }
        if (requestHeaders != null) {
            stringBuilder.append("\"requestHeaders\":{");
            stringBuilder.append(requestHeaders.toString());
            stringBuilder.append("},");
        }
        if (responseHeaders != null) {
            stringBuilder.append("\"responseHeaders\":{");
            stringBuilder.append(responseHeaders.toString());
            stringBuilder.append("},");
        }
        stringBuilder.append("\"key\":\"");
        stringBuilder.append(key);
        stringBuilder.append("\"");
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        Traffic traffic = new Traffic();
        traffic.setKey("123");
        traffic.setDirection("up");
        traffic.setHost("www.baidu.com");
        traffic.setMethod("get");
//        traffic.setReqDate(123456789);
        traffic.setRespDate(1531515656);
        traffic.setReqBodyLength(1212);
        traffic.setRespBodyLength(4444);
        traffic.setUrl("/dasiuhdasuid");
        traffic.setRequestBody("请求体");
        traffic.setResponseBody("响应体");
        MyMap m1 = new MyMap();
        m1.put("key1", "value1");
        m1.put("key2", "value2");
        m1.put("key3", "value3");
        m1.put("key4", "value4");
//        traffic.setRequestHeaders(m1);
        MyMap m2 = new MyMap();
        m2.put("keya", "valuea");
        m2.put("keyb", "valueb");
        m2.put("keyc", "valuec");
        m2.put("keyd", "valued");
        traffic.setResponseHeaders(m2);
        System.out.println(traffic.toString());
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setRequestHeaders(MyMap requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public MyMap getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(MyMap responseHeaders) {
        this.responseHeaders = responseHeaders;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public long getReqDate() {
        return reqDate;
    }

    public void setReqDate(long reqDate) {
        this.reqDate = reqDate;
    }

    public long getRespDate() {
        return respDate;
    }

    public void setRespDate(long respDate) {
        this.respDate = respDate;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getReqBodyLength() {
        return reqBodyLength;
    }

    public void setReqBodyLength(long reqBodyLength) {
        this.reqBodyLength = reqBodyLength;
    }

    public long getRespBodyLength() {
        return respBodyLength;
    }

    public void setRespBodyLength(long respBodyLength) {
        this.respBodyLength = respBodyLength;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }


    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
