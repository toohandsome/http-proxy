package io.github.toohandsome.attach.entity;

import java.util.HashMap;
import java.util.Set;

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

//    public static void main(String[] args) {
//        String us = "\" Chromiu\r\nm \";\rv=\" 1\n10 \", \" Not A(B";
//        System.out.println(us);
//        System.out.println("----------------");
//        String s = us.replace("\"", "\\\"").replaceAll("\r","\\\\r").replaceAll("\n","\\\\n");
//        System.out.println(s);
//    }
}
