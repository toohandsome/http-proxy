package io.github.toohandsome.httproxy.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Rule {

    /**
     * 00 修改请求头
     * 10 增加请求头
     * -10 删除请求头
     * <p>
     * 01 修改请求体
     * 11 增加请求体
     * -11 删除请求体
     * <p>
     * 02 修改响应头
     * 12 增加响应头
     * -12 删除响应头
     * <p>
     * 03 修改响应体
     * 13 增加响应体
     * -13 删除响应体
     */
    private int mode;


    private String headerName;

    /**
     * 表示新增的内容   或原始内容
     */
    private String source;

    /**
     * 替换和新增时使用
     */
    private String content;

    static List<Integer> reqModeList = Arrays.asList(0, 10, -10, 1, 11, -11);
    static List<Integer> respModeList = Arrays.asList(2, 12, -12, 3, 13, -13);
    static List<Integer> headerModeList = Arrays.asList(0, 10, -10, 2, 12, -12);
    static List<Integer> bodyModeList = Arrays.asList(1, 11, -11, 3, 13, -13);

    public static boolean isReqOpt(int mode) {

        if (reqModeList.contains(mode)) {
            return true;
        }
        return false;
    }

    public static boolean isRespOpt(int mode) {

        if (respModeList.contains(mode)) {
            return true;
        }
        return false;
    }

    public static boolean isHeaderOpt(int mode) {

        if (headerModeList.contains(mode)) {
            return true;
        }
        return false;
    }


    public static boolean isBodyOpt(int mode) {

        if (bodyModeList.contains(mode)) {
            return true;
        }
        return false;
    }

    public static List<Rule> getRule(List<Rule> ruleList, int... modes) {
        return ruleList.stream().filter(rule -> {
            for (int i = 0; i < modes.length; i++) {
                if (rule.getMode() == modes[i]) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }
}
