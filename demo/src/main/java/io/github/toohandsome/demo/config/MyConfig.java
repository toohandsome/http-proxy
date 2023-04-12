package io.github.toohandsome.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
/**
 * @author hudcan
 */
@Configuration
public class MyConfig {

    @Bean
    public RestTemplate restTemplate1(){
        return new RestTemplate();
    }


    @Bean
    public RestTemplate restTemplate2(){
        return new RestTemplate();
    }

}
