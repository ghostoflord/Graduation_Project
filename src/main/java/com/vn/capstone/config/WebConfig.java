package com.vn.capstone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/avatars/**")
                .addResourceLocations("file:E:/Graduation_Project/upload/avatars/");
        registry.addResourceHandler("/upload/products/**")
                .addResourceLocations("file:E:/Graduation_Project/upload/products/");
    }

}
