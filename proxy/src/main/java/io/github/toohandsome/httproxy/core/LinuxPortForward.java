package io.github.toohandsome.httproxy.core;


import io.github.toohandsome.httproxy.util.RuntimeUtil;

/**
 * @author toohandsome
 */
public class LinuxPortForward implements PortForward {

    String cmdStr = "iptables -t nat -I PREROUTING -p tcp --dport ? -j DNAT --to-destination ?:?";
    String cmdStr1 = "echo 1 >/proc/sys/net/ipv4/ip_forward";
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
