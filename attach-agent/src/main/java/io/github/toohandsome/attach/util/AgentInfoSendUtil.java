package io.github.toohandsome.attach.util;

import io.github.toohandsome.attach.entity.AgentEntity;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class AgentInfoSendUtil {

    static LinkedBlockingQueue<String> agentInfoQueue = new LinkedBlockingQueue(1000);

    public static void send(AgentEntity agentEntity) {
        String infoStr = agentEntity.toString();
        String finalStr = infoStr.substring(0, infoStr.length() - 1) + "\"bussType\":\"" + agentEntity.getClass().getSimpleName() + "\"" + "$_httpProxy_$";
        agentInfoQueue.offer(finalStr);
    }

    static {
        new Thread(() -> {
            while (true) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new Socket("localhost", 8009).getOutputStream()))) {
                    for (int i = 0; i < 100; i++) {
                        String agentInfo = agentInfoQueue.poll();
                        writer.write(agentInfo);
                        writer.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
