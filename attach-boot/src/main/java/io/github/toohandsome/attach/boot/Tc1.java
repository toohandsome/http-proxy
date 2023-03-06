package io.github.toohandsome.attach.boot;

import com.sun.tools.attach.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class Tc1 {

    @GetMapping("/attach")
    public String attach()  {


        List<VirtualMachineDescriptor> list = VirtualMachine.list(); // 寻找当前系统中所有运行着的JVM进程
        for (VirtualMachineDescriptor vmd : list) {
            //如果虚拟机的名称为 xxx 则 该虚拟机为目标虚拟机，获取该虚拟机的 pid
            //然后加载 agent.jar 发送给该虚拟机
            System.out.println(vmd.displayName()); //vmd.displayName()看到当前系统都有哪些JVM进程在运行
            if (vmd.displayName().endsWith("DemotestApplication")) {
                VirtualMachine virtualMachine = null;
                try {
                    virtualMachine = VirtualMachine.attach(vmd.id());
                    virtualMachine.loadAgent("F:\\http-proxy\\attach-agent\\target\\attach-agent-1.0.0.jar");
                    virtualMachine.detach();
                } catch (AttachNotSupportedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AgentLoadException e) {
                    e.printStackTrace();
                } catch (AgentInitializationException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return "success";
    }
}
