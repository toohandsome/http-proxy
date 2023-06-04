package io.github.toohandsome.demo;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.apache.http.client.config.RequestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * @author toohandsome
 */
//@EnableApolloConfig
@SpringBootApplication
public class DemotestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemotestApplication.class, args);
    }

}
