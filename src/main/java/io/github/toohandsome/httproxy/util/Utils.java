package io.github.toohandsome.httproxy.util;

import com.alibaba.fastjson2.JSON;
import io.github.toohandsome.httproxy.entity.Route;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Utils {

    public static List loadRoutes()   {
        List<Route> arrayList = new ArrayList();

        List<String> strings;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            strings = Files.readAllLines(Paths.get("route.json"));
            for (String string : strings) {
                stringBuilder.append(string);
            }
            final List<Route> routes = JSON.parseArray(stringBuilder.toString(), Route.class);
            arrayList = routes == null ? new ArrayList<>() : routes;

            return arrayList;
        } catch (Exception e) {
            try {
                Files.createFile(Paths.get("route.json"));
            }catch (Exception exception){
                e.printStackTrace();
            }

            logger.warn("配置文件 route.json 不存在");
        }
        return arrayList;
    }


}
