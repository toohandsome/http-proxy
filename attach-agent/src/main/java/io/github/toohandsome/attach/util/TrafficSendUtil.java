package io.github.toohandsome.attach.util;

import io.github.toohandsome.attach.entity.Traffic;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.Socket;

public class TrafficSendUtil {

    public static void send(Traffic traffic) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new Socket("localhost", 8009).getOutputStream()))) {
            writer.write(traffic.toString());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
