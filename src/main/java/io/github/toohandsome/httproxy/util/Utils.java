package io.github.toohandsome.httproxy.util;

import com.alibaba.fastjson2.JSON;
import io.github.toohandsome.httproxy.ProxyServlet;
import io.github.toohandsome.httproxy.controller.RouteController;
import io.github.toohandsome.httproxy.entity.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;

import javax.servlet.ServletContext;
import java.lang.reflect.Field;
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
            for (Route route : routes) {
                addServelet(route);
            }
            RouteController.routes = arrayList;
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
    public static boolean addServelet(Route route) {
        try {
            final ServletContext servletContext = SpringUtil.getBean(ServletContext.class);
            Field appctx = servletContext.getClass().getDeclaredField("context");
            appctx.setAccessible(true);
            ApplicationContext applicationContext = (ApplicationContext) appctx.get(servletContext);
            Field stdctx = applicationContext.getClass().getDeclaredField("context");
            stdctx.setAccessible(true);
            StandardContext standardContext = (StandardContext) stdctx.get(applicationContext);
            Wrapper wrapper = standardContext.createWrapper();
            wrapper.setName(route.getName());
            wrapper.setLoadOnStartup(1);
            final ProxyServlet servlet = new ProxyServlet();
            if (route.getRuleList() != null) {
                servlet.ruleList = route.getRuleList();
            }
            servlet.targetUri = route.getRemote();
            wrapper.setServlet(servlet);
            wrapper.setServletClass(servlet.getClass().getName());
            standardContext.addChild(wrapper);
            standardContext.addServletMappingDecoded("/" + route.getPrefix() + "/*", route.getName());
            logger.info("注册成功: " + route.getName());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("注册失败: " + JSON.toJSONString(route));
        }
        return false;
    }

}
