package io.github.toohandsome.httproxy.entity;

import lombok.Data;

import java.util.List;

@Data
public class Route {
    private String name;
    private String remark;
    private String prefix;
    private String remote;
    private String mode;
    private List<Rule> ruleList;
}
