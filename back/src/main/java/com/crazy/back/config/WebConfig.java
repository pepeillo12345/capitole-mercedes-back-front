package com.crazy.back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // rutas expuestas
                        .allowedOrigins("http://localhost:4200") // origen del front
                        .allowedMethods("GET") // métodos permitidos
                        .allowedHeaders("*")
                        .allowCredentials(true); // si necesitas cookies/autenticación
            }
        };
    }
}
