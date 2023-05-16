package io.github.toohandsome.attach.core;

import io.github.toohandsome.attach.util.RuntimeUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author toohandsome
 */
public class WindowsPortForward implements PortForward {

    String cmdStr = "netsh interface portproxy add v4tov4 listenaddress=127.0.0.1 listenport=? connectaddress=? connectport=?";

    @Override
    public void forward(int localPort, int remotePort) {
        String format = String.format(cmdStr, localPort, "127.0.0.1", remotePort);
        try {
            Process exec = Runtime.getRuntime().exec(format);
            int i = exec.waitFor();
            RuntimeUtil.getOutStr(exec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void forward(int localPort, String remoteAddr, int remotePort) {
        String format = String.format(cmdStr, localPort, remoteAddr, remotePort);
        try {
            Process exec = Runtime.getRuntime().exec(format);
            int i = exec.waitFor();
            RuntimeUtil.getOutStr(exec);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void reset() {

    }
}
