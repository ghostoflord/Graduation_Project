package com.vn.capstone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/", "/api/v1/auth/**", "/storage/**",
                "/api/v1/products/**", "/api/v1/files", "/api/v1/carts/**",
                "/api/v1/orders/**", "/api/v1/dashboard/**", "/api/v1/auth/resend-verification", "/api/v1/chat",
                "/api/v1/auth/send-reset-otp",
                "/api/v1/product-details/**", "/api/v1/comments/**", "/api/v1/reviews/**", "/api/v1/likes/**",
                "/api/v1/compare/**", "/api/v1/flash-sales/**",
                "/api/v1/notifications/**", "/api/v1/invoice/**", "/api/v1/manual-chat/**", "/api/v1/vouchers/**",
                "/api/v1/permissions/**", "/upload/avatars/**", "/upload/products/**", "/api/v1/payment/**",
                "/api/v1/manual-chats/**", "/api/v1/slides/**", "/api/v1/statistics/**",
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}