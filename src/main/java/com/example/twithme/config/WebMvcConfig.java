package com.example.twithme.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("Content-Type", "X-AUTH-TOKEN", "Authorization")
                .allowedMethods(HttpMethod.POST.name(), HttpMethod.GET.name(), HttpMethod.DELETE.name(), HttpMethod.PUT.name(), HttpMethod.PATCH.name(), HttpMethod.OPTIONS.name())
                .allowCredentials(false)
                .exposedHeaders("Authorization", "X-AUTH-TOKEN")
                .maxAge(3000);
    }
}
