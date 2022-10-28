package io.github.toohandsome.httproxy;


import io.github.toohandsome.httproxy.util.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Spring boot app.
 *
 * @author shuaicj 2017/09/21
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        Utils.loadRoutes();
    }
}
