package io.github.toohandsome.httproxy.controller;

import io.github.toohandsome.httproxy.entity.Route;
import io.github.toohandsome.httproxy.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;


@RestController
@RequestMapping("/httpProxy/routeApi")
@Slf4j
public class RouteController {


    /**
     * 参数是为了兼容 全部转发时反射获取方法和执行方法,无作用
     *
     * @param route
     * @return
     * @throws IOException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @PostMapping("/getRouteList")
    public List<Route> getRouteList(@RequestBody Route route) {
        if (Utils.routes.isEmpty()) {
            Utils.routes = Utils.loadRoutes();
        }
        return Utils.routes;
    }

    @PostMapping("/editProxy")
    public boolean editProxy(@RequestBody Route route) {
        try {
            if (delProxy(route)) {
                return addProxy(route);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("/delProxy")
    public boolean delProxy(@RequestBody Route route) {
        try {
            Iterator<Route> iterator = Utils.routes.iterator();
            while (iterator.hasNext()) {
                Route next = iterator.next();
                if (next.getName().equals(route.getName())) {
                    Utils.routes.remove(next);
                    Utils.saveRoute();
                    Utils.removeServelet(route);
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
        if (!StringUtils.hasText(route.getName()) || !StringUtils.hasText(route.getPrefix())) {
            return false;
        }
        if (Utils.addServelet(route)) {
            Utils.routes.add(route);
            Utils.saveRoute();
            return true;
        }
        return false;
    }


}
