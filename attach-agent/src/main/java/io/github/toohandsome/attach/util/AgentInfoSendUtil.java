package io.github.toohandsome.attach.util;

import io.github.toohandsome.attach.entity.AgentEntity;
import io.github.toohandsome.attach.entity.AgentInfo;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class AgentInfoSendUtil {

//    static LinkedBlockingQueue<String> agentInfoQueue = new LinkedBlockingQueue(1000);

    public static void send(AgentEntity agentEntity) {
        String infoStr = agentEntity.toString();
        String finalStr = infoStr.substring(0, infoStr.length() - 1) + ",\"bussType\":\"" + agentEntity.getClass().getSimpleName() + "\"}" + "$_httpProxy_$";
//        agentInfoQueue.offer(finalStr);
    }

    public static void sendInfo(String msg) {
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setMsg(msg);
        agentInfo.setType("info");
        send(agentInfo);
    }

    public static void sendExcepTion(Exception exception) {
        AgentInfo agentInfo = new AgentInfo();
        String message = exception.getMessage();
        StringBuilder stringBuilder = new StringBuilder(message);
        stringBuilder.append("\r\n");
        StackTraceElement[] stackTrace = exception.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            stringBuilder.append("\t" + stackTrace[i].toString() + "\r\n");
        }
        agentInfo.setMsg(stringBuilder.toString());
        agentInfo.setType("error");
        send(agentInfo);
    }

//    static {
//        new Thread(() -> {
//            while (true) {
//                ArrayList<String> list = new ArrayList();
//                for (int i = 0; i < 100; i++) {
//                    String agentInfo = agentInfoQueue.poll();
//                    if (agentInfo != null) {
//                        list.add(agentInfo);
//                    }
//                }
//                if (!list.isEmpty()) {
//                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new Socket("localhost", 8009).getOutputStream()))) {
//                        for (String str : list) {
//                            writer.write(str);
//                            writer.flush();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
}
