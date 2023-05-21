package io.github.toohandsome.httproxy.core;

import io.github.toohandsome.attach.util.RuntimeUtil;

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
