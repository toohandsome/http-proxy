package io.github.toohandsome.attach.entity;

import java.util.HashMap;
import java.util.Set;
/**
 * @author toohandsome
 */
public class MyMap extends HashMap {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        Set set = this.keySet();
        for (Object key : set) {
            sb.append("\"" + key + "\":\"" + (this.get(key)+"").replace("\"","\\\"") + "\",");
        }
        String s = sb.toString();
        if (s.endsWith(",")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static void main(String[] args) {
        String us = "\" Chr\\omiu\r\nm \";\rv=\" 1\n10 \", \" N\\ot A(B";
        System.out.println(us);
        System.out.println("----------------");
        String s = us.replace("\\", "\\u000").replace("\"", "\\\"").replaceAll("\r","\\\\r").replaceAll("\n","\\\\n");
        System.out.println(s);
    }
}
