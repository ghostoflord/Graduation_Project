package com.vn.capstone.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.MediaType;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/avatars/**")
                .addResourceLocations("file:E:/Graduation_Project/upload/avatars/");
        registry.addResourceHandler("/upload/products/**")
                .addResourceLocations("file:E:/Graduation_Project/upload/products/");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof AbstractJackson2HttpMessageConverter jacksonConverter) {
                jacksonConverter.setSupportedMediaTypes(List.of(
                        MediaType.APPLICATION_JSON,
                        MediaType.valueOf("application/json;charset=UTF-8")));
            }
        }
    }
}
