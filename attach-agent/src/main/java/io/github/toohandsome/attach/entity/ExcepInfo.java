package io.github.toohandsome.attach.entity;

public class ExcepInfo extends AgentEntity {
    private String type;
    private String msg;


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("{");

        stringBuilder.append("\"type\":");
        stringBuilder.append(type);
        stringBuilder.append("\",");
        stringBuilder.append("\"msg\":");
        stringBuilder.append(msg.replace("\\", "\\\\").replace("\"","\\\"").replaceAll("\r","\\\\r").replaceAll("\n","\\\\n"));
        stringBuilder.append("\"");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
