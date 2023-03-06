package io.github.toohandsome.attach.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InputStreamUtil {

    public static List<InputStream> cloneInputStream(InputStream input, Integer count) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        InputStream byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
        List<InputStream> inputStreamList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            inputStreamList.add(new ByteArrayInputStream(baos.toByteArray()));
        }
        return inputStreamList;
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
