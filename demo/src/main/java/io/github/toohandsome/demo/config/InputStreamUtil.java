package io.github.toohandsome.demo.config;

import sun.net.www.http.ChunkedInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class InputStreamUtil {

    public static InputStream cloneInputStream(InputStream input) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[10240];
        int len;
        while ((len = input.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        System.out.println(new String(baos.toByteArray(), StandardCharsets.UTF_8));
        return new ByteArrayInputStream(baos.toByteArray());

    }


    public static void readInputStream(InputStream inputStream) throws IOException {
        inputStream.mark(0);
        StringBuffer resultBuffer = new StringBuffer();
        String line;
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
        while ((line = buffer.readLine()) != null) {
            resultBuffer.append(line);
        }
        System.out.println("result:" + resultBuffer.toString());
        inputStream.reset();

    }
}
