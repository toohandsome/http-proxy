package io.github.toohandsome.attach.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * @author hudcan
 */
public class AgentInfo extends AgentEntity {
    private String type;
    private String msg;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("{");
        stringBuilder.append("\"type\":\"");
        stringBuilder.append(type);
        stringBuilder.append("\",");
        stringBuilder.append("\"msg\":\"");
        stringBuilder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + "\t" + msg.replace("\\", "\\\\").replace("\"", "\\\"").replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
        stringBuilder.append("\"");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public String getType() {
        return type;
    }

    public AgentInfo setType(String type) {
        this.type = type;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public AgentInfo setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
