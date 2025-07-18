package com.vn.capstone.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PageableConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        // Cấu hình custom tên query param
        pageableResolver.setPageParameterName("current"); // thay vì "page"
        pageableResolver.setSizeParameterName("pageSize"); // thay vì "size"
        pageableResolver.setOneIndexedParameters(true); // current = 1 là page 0

        resolvers.add(pageableResolver);
    }
}
