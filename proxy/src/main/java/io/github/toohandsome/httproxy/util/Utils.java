package io.github.toohandsome.httproxy.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import io.github.toohandsome.httproxy.core.ProxyServlet;
import io.github.toohandsome.httproxy.entity.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.http.Header;

import javax.servlet.ServletContext;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Utils {

    public static ConcurrentHashMap<String, Container> wrapperMap = new ConcurrentHashMap<>();
    public static List<Route> routes = new ArrayList<>();

    public static String getContentCharset(Header header) {
        String charset = "utf-8";
        if (header != null) {
            String s = header.getValue();
            if (s.toLowerCase().contains(StandardCharsets.ISO_8859_1.name().toLowerCase())) {
                charset = StandardCharsets.ISO_8859_1.name();
            } else if (s.toLowerCase().contains("gbk")) {
                charset = "gbk";
            } else if (s.toLowerCase().contains("gb2312")) {
                charset = "gb2312";
            }
        }

        return charset;
    }

    public static List loadRoutes() {
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
            for (Route route : routes) {
                addServelet(route);
            }
            Utils.routes = arrayList;
            return arrayList;
        } catch (Exception e) {
            try {
                Files.createFile(Paths.get("route.json"));
            } catch (Exception exception) {
                e.printStackTrace();
            }

            logger.warn("配置文件 route.json 不存在");
        }
        return arrayList;
    }

    public static boolean removeServelet(Route route) {
        Container container = wrapperMap.get(route.getName());
        if (container != null) {
            standardContext.removeChild(container);
        }
        return true;
    }

    public static boolean saveRoute() {

        String prettyStr = JSON.toJSONString(routes, JSONWriter.Feature.PrettyFormat);
        try {
            Files.write(Paths.get("route.json"), prettyStr.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    static StandardContext standardContext = null;

    public static boolean addServelet(Route route) {
        try {
            if (!route.getName().equals("ALL")) {
                return true;
            }
            route.setPrefix("*");
            final ServletContext servletContext = SpringUtil.getBean(ServletContext.class);

            if (standardContext == null) {
                Field appctx = servletContext.getClass().getDeclaredField("context");
                appctx.setAccessible(true);
                ApplicationContext applicationContext = (ApplicationContext) appctx.get(servletContext);
                Field stdctx = applicationContext.getClass().getDeclaredField("context");
                stdctx.setAccessible(true);
                standardContext = (StandardContext) stdctx.get(applicationContext);
            }

            Wrapper wrapper = standardContext.createWrapper();
            wrapper.setName(route.getName());
            wrapper.setLoadOnStartup(1);
            final ProxyServlet proxyServlet = new ProxyServlet();
            if (route.getRuleList() != null) {
                proxyServlet.ruleList = route.getRuleList();
            }
            proxyServlet.targetUri = route.getRemote();
            wrapper.setServlet(proxyServlet);
            wrapper.setServletClass(proxyServlet.getClass().getName());
            standardContext.addChild(wrapper);
            String prefix = route.getPrefix();

            if (prefix.equals("*") || prefix.equals("")) {
                standardContext.addServletMappingDecoded("/*", route.getName());
            } else {
                standardContext.addServletMappingDecoded(prefix + "/*", route.getName());
            }
            logger.info("注册成功: " + route.getName());

            wrapperMap.put(route.getName(), wrapper);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("注册失败: " + JSON.toJSONString(route));
        }
        return false;
    }

}
