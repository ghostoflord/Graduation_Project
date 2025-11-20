package com.vn.capstone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    private final PermissionInterceptor permissionInterceptor;
    private final PublicApiMatcher publicApiMatcher;

    public PermissionInterceptorConfiguration(PermissionInterceptor permissionInterceptor,
            PublicApiMatcher publicApiMatcher) {
        this.permissionInterceptor = permissionInterceptor;
        this.publicApiMatcher = publicApiMatcher;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns(publicApiMatcher.getAnyMethodPatterns());
    }
}