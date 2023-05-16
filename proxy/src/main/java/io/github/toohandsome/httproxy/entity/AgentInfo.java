package io.github.toohandsome.httproxy.entity;
/**
 * @author toohandsome
 */
public class AgentInfo extends AgentEntity {
    private String type;
    private String msg;



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
