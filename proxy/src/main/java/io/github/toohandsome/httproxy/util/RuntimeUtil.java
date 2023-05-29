package io.github.toohandsome.httproxy.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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

            InputStream errorStream = process.getErrorStream();
            BufferedReader bufferedReader1 = null;
            try {
                bufferedReader1 = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"));
                String line1;
                while ((line1 = bufferedReader1.readLine()) != null) {
                    sb.append(line1);
                }
            } catch (Exception ex) {
               e.printStackTrace();
            }

        }
        String string = sb.toString();

        return string;
    }

}
