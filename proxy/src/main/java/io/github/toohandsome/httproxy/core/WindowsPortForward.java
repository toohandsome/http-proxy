package io.github.toohandsome.httproxy.core;


import io.github.toohandsome.httproxy.util.RuntimeUtil;

/**
 * @author toohandsome
 */
public class WindowsPortForward implements PortForward {

    String cmdStr = "New-NetNat -Name \"NAT1\" -InternalIPInterfaceAddressPrefix \"192.168.0.0/24\" -ExternalIPAddress \"192.0.2.100\"\n";
//    String cmdStr = "netsh interface portproxy add v4tov4 listenaddress=127.0.0.1 listenport=%s connectaddress=%s connectport=%s";

    @Override
    public void forward(int localPort, int remotePort) {
        String format = String.format(cmdStr, localPort, "127.0.0.1", remotePort);
        try {
            Process exec = Runtime.getRuntime().exec(format);
            int i = exec.waitFor();
            String outStr = RuntimeUtil.getOutStr(exec);
            System.out.println("outStr: "+ outStr);
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
            String outStr = RuntimeUtil.getOutStr(exec);
            System.out.println("outStr: "+ outStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void reset() {

    }
}
