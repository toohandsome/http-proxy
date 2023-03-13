package io.github.toohandsome.attach.util;

import sun.net.www.http.ChunkedInputStream;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.nio.charset.StandardCharsets;

public class InputStreamUtil {

    public static InputStream cloneInputStream(InputStream input,Object o1,Object o2) throws Exception {
        if (input instanceof ChunkedInputStream) {
//            input = new GZIPInputStream(input);
            return input;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        System.out.println(new String(baos.toByteArray(), StandardCharsets.UTF_8));
        return new ByteArrayInputStream(baos.toByteArray());

    }

    public static InputStream cloneInputStream(InputStream input, InputStream input2) throws Exception {

        if (!(input2 instanceof ChunkedInputStream)) {
            return input;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();

        InputStream input1 = new GZIPInputStream(new ByteArrayInputStream(baos.toByteArray()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(input1, "utf-8"));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        System.out.println(sb);
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
