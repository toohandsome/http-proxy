package io.github.toohandsome.httproxy.entity;

import lombok.Data;
/**
 * @author toohandsome
 */
@Data
public class AgentOpt {

    private String type;
    private String val;
    private String opt;
    private String port;
    private String proxyPort;
    private boolean getStack = false;
    private boolean proxy = false;
}
