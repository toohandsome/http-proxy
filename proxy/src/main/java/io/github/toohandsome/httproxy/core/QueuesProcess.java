package io.github.toohandsome.httproxy.core;


import com.alibaba.fastjson.JSON;
import io.github.toohandsome.httproxy.controller.WebSocketServer;
import io.github.toohandsome.httproxy.entity.AgentInfo;
import io.github.toohandsome.httproxy.entity.Traffic;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class QueuesProcess implements CommandLineRunner {
    public static LinkedBlockingQueue<Traffic> trafficQueue = new LinkedBlockingQueue(1000);

    public static LinkedBlockingQueue<AgentInfo> agentInfoQueue = new LinkedBlockingQueue(1000);

    public void processQueueData() {

        new Thread(() -> {
            while (true) {
                try {
                    pollDataFromQueue(trafficQueue);
                    pollDataFromQueue(agentInfoQueue);
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

    public void pollDataFromQueue(LinkedBlockingQueue queue) throws IOException {
        ArrayList list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Object poll = queue.poll();
            if (poll != null) {
                list.add(poll);
            }
        }
        if (!list.isEmpty()) {
            WebSocketServer.sendInfo(JSON.toJSONString(list), null);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        processQueueData();
    }
}
