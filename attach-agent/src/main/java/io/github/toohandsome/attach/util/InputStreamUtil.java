package io.github.toohandsome.attach.util;

import sun.net.www.MessageHeader;
import sun.net.www.http.ChunkedInputStream;
import sun.net.www.http.PosterOutputStream;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.nio.charset.StandardCharsets;

public class InputStreamUtil {

    public static void getRequestInfo(MessageHeader header, PosterOutputStream var2) throws Exception {
        Field keys = header.getClass().getDeclaredField("keys");
        Field values = header.getClass().getDeclaredField("values");
        keys.setAccessible(true);
        values.setAccessible(true);
        String[] keysArr = (String[]) keys.get(header);
        String[] valuesArr = (String[]) values.get(header);
        for (int i = 0; i < keysArr.length; i++) {
            if (keysArr[i] == null && valuesArr[i] == null) {
                continue;
            }
            System.out.println("req key: " + keysArr[i] + "  --  value: " + valuesArr[i]);
        }
        System.out.println("req body: " + var2.toString());
    }

    public static InputStream cloneInputStream(InputStream input, Object o1, Object o2) throws Exception {

        if (input instanceof ChunkedInputStream) {
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
