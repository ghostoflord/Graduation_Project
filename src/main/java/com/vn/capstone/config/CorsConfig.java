package com.vn.capstone.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                Arrays.asList("http://localhost:3000", "http://localhost:4173", "http://localhost:5173",
                        "http://192.168.10.103:3000"));
        // các methob nào được kết nối
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed methods
        // các phần header được phép gửi lên
        configuration
                .setAllowedHeaders(
                        Arrays.asList("Authorization", "Content-Type", "Accept", "x-no-retry", "delay", "upload-type"));
        // có gửi kèm cookies hay không
        configuration.setAllowCredentials(true);
        // thời gian pe-fight request có thể cache
        configuration.setMaxAge(3600L);
        // How long the response from a pre-flight request can be cached by clients
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // cấu hình cors cho tất cả các api
        source.registerCorsConfiguration("/**", configuration);
        // Apply this configuration to all paths
        return source;
    }

}