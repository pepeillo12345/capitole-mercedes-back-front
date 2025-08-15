package com.crazy.back.config;

import com.crazy.back.clients.swapi.feign.FeignLogger;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Just in case I need to configure Feign clients in the future.
 * Currently, no specific configuration is needed.
 */
@Configuration
public class SwFeignConfig {

    @Bean
    public Logger feignLogger() {
        return new FeignLogger();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
