package io.github.toohandsome.httproxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class ResourceConfig {
    @Bean
    public DefaultResourceLoader classPathResourceLoader() {
        return new DefaultResourceLoader();
    }


}
