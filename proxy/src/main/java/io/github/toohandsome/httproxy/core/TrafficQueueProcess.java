package io.github.toohandsome.httproxy.core;

import com.alibaba.fastjson2.JSON;
import io.github.toohandsome.httproxy.controller.WebSocketServer;
import io.github.toohandsome.httproxy.entity.Traffic;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TrafficQueueProcess implements CommandLineRunner {
    public static LinkedBlockingQueue<Traffic> trafficQueue = new LinkedBlockingQueue(1000);

    public void processTraffice() {

        new Thread(() -> {
            while (true) {
                try {
                    ArrayList<Traffic> list = new ArrayList<>();
                    for (int i = 0; i < 100; i++) {
                        Traffic poll = trafficQueue.poll();
                        if (poll != null) {
                            list.add(poll);
                        }
                    }
                    if (!list.isEmpty()) {
                        WebSocketServer.sendInfo(JSON.toJSONString(list), null);
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

    @Override
    public void run(String... args) throws Exception {
        processTraffice();
    }
}
