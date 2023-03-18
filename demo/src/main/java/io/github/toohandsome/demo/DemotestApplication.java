package io.github.toohandsome.demo;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.apache.http.client.config.RequestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableApolloConfig
@SpringBootApplication
public class DemotestApplication {

    public static void main(String[] args) {
        System.out.println("111");
        SpringApplication.run(DemotestApplication.class, args);

    }

}
