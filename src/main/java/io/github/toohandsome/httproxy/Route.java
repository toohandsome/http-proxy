package io.github.toohandsome.httproxy;

import lombok.Data;

@Data
public class Route {
    private String name;
    private String remark;
    private String prefix;
    private String remote;
    private String mode;
}
