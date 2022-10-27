package io.github.toohandsome.httproxy;


import io.github.toohandsome.httproxy.util.Utils;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.lang.reflect.Field;

/**
 * Spring boot app.
 *
 * @author shuaicj 2017/09/21
 */
@SpringBootApplication

//@re

@RestController
public class Application {

    static ConfigurableApplicationContext ca = null;


    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        final ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
        ca = run;
        Utils.loadRoutes();
    }
}
