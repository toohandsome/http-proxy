package io.github.toohandsome.attach.util;

import java.io.*;

public class RuntimeUtil {

    public static String getOutStr(Process process) {
        InputStream inputStream = process.getInputStream();

        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader1 = null;
            bufferedReader1 = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line1;
            while ((line1 = bufferedReader1.readLine()) != null) {
                sb.append(line1);
            }
        } catch (Exception e) {
            AgentInfoSendUtil.sendExcepTion(e);
            InputStream errorStream = process.getErrorStream();
            BufferedReader bufferedReader1 = null;
            try {
                bufferedReader1 = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"));
                String line1;
                while ((line1 = bufferedReader1.readLine()) != null) {
                    sb.append(line1);
                }
            } catch (Exception ex) {
                AgentInfoSendUtil.sendExcepTion(ex);
            }

        }
        String string = sb.toString();
        AgentInfoSendUtil.sendInfo(string);
        return string;
    }

}
