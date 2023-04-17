package io.github.toohandsome.attach.core;

import io.github.toohandsome.attach.util.AgentInfoSendUtil;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ResetListener {
    public static void startListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(10086);
                    //监听（阻塞）
                    Socket socket = serverSocket.accept();
                    //获取输入流对象
                    InputStream is = socket.getInputStream();
                    //获取数据
                    byte[] bys = new byte[1024];
                    int len;    //用于存储读到的字节个数
                    len = is.read(bys);
                    //输出数据
                    AgentInfoSendUtil.sendInfo("agent 收到命令 : " + new String(bys, 0, len));
                    Reset.reset();
                    if (is != null) {
                        is.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                    AgentInfoSendUtil.sendInfo("重置增强类,监听结束 .. ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
