package com.homework4.workapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Path.of(System.getProperty("user.home"), "workapi-uploads");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir.toAbsolutePath() + "/");
    }
}