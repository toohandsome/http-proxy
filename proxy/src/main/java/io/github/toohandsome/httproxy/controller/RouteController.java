package io.github.toohandsome.httproxy.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import io.github.toohandsome.httproxy.entity.Route;
import io.github.toohandsome.httproxy.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@Slf4j
public class RouteController {

    public static List<Route> routes = new ArrayList<>();


    @GetMapping("/getRouteList")
    public List<Route> getRouteList() throws IOException, NoSuchFieldException, IllegalAccessException {
        if (routes.isEmpty()) {
            routes = Utils.loadRoutes();
        }
        return routes;
    }

    @PostMapping("/editProxy")
    public boolean editProxy(@RequestBody Route route) {

        try {
            for (Route route1 : routes) {
                if (route1.getName().equals(route.getName())) {
                    route1 = route;
                    saveRoute();
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @PostMapping("/delProxy")
    public boolean delProxy(@RequestBody Route route) {
        try {
            Iterator<Route> iterator = routes.iterator();
            while (iterator.hasNext()) {
                Route next = iterator.next();
                if (next.getName().equals(route.getName())) {
                    routes.remove(next);
                    saveRoute();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }




    @PostMapping("/addProxy")
    public boolean addProxy(@RequestBody Route route) throws NoSuchFieldException, IllegalAccessException {

        if (Utils.addServelet(route)) {
            routes.add(route);
            saveRoute();
            return true;
        }
        return false;
    }

    public boolean saveRoute() {

        String prettyStr = JSON.toJSONString(routes, JSONWriter.Feature.PrettyFormat);
        try {
            Files.write(Paths.get("route.json"), prettyStr.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
