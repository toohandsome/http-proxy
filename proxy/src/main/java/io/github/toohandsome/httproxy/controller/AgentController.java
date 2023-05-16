package io.github.toohandsome.httproxy.controller;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import io.github.toohandsome.httproxy.entity.AgentOpt;
import io.github.toohandsome.httproxy.entity.Route;
import io.github.toohandsome.httproxy.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

/**
 * @author toohandsome
 */
@Component
@RequestMapping("/httpProxy/agentApi")
@Slf4j
public class AgentController {
    VirtualMachine virtualMachine1 = null;
    @Value("${whiteListPath:whiteList.txt}")
    String whiteListPath;

    public boolean attach(AgentOpt agentOpt) {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        ApplicationHome h = new ApplicationHome(getClass());
        File jarF = h.getSource();
        String path = jarF.getParentFile().toString();
        String args = agentOpt.getPort() + ";" + whiteListPath + ";" + agentOpt.isGetStack() + ";" + agentOpt.isProxy() + ";" + agentOpt.getProxyPort();

        for (VirtualMachineDescriptor vmd : list) {

            System.out.println("pid: " + vmd.id() + " --> process: " + vmd.displayName());
            if ("processId".equals(agentOpt.getType())) {
                try {
                    if (vmd.id().equals(agentOpt.getVal())) {
                        VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
                        virtualMachine.loadAgent(path + File.separator + "attach-agent-1.0.0.jar", args);
                        virtualMachine.detach();
                        System.out.println("attach " + vmd.displayName() + " success");
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else if ("processName".equals(agentOpt.getType())) {
                if (vmd.displayName().contains(agentOpt.getVal())) {
                    VirtualMachine virtualMachine = null;
                    try {
                        virtualMachine = VirtualMachine.attach(vmd.id());
                        virtualMachine.loadAgent(path + File.separator + "attach-agent-1.0.0.jar", args);
                        virtualMachine1 = virtualMachine;
                        virtualMachine.detach();
                        System.out.println("attach " + vmd.displayName() + " success");
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean reset(AgentOpt agentOpt) throws Exception {
        try {
            virtualMachine1.detach();
            //创建发送端Socket对象（创建连接）
            Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 10086);
            //获取输出流对象
            OutputStream os = socket.getOutputStream();
            //发送数据
            String str = "Hi,TCP!";
            os.write(str.getBytes());
            //释放资源
            os.close();
            socket.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


}
