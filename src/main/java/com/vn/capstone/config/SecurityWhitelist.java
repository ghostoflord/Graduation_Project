package com.vn.capstone.config;

import java.util.Arrays;

import org.springframework.util.AntPathMatcher;

/**
 * Centralized list of endpoints that should remain publicly accessible without
 * authentication. Supports both method-agnostic and GET-only patterns.
 */
public final class SecurityWhitelist {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private SecurityWhitelist() {
    }

    /**
     * Public endpoints that are accessible for every HTTP method.
     */
    public static final String[] PUBLIC_ENDPOINTS = {
            "/",
            "/api/v1/auth/**",
            "/api/v1/verify/**",
            "/oauth2/**",
            "/upload/avatars/**",
            "/upload/products/**",
            "/upload/slides/**",
            "/api/v1/payment/vnpay/response"
    };

    /**
     * Endpoints that are public for GET requests (used for storefront data).
     */
    public static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/v1/products/**",
            "/api/v1/product-details/**",
            "/api/v1/slides/**",
            "/api/v1/flash-sales/**",
            "/api/v1/vouchers/**",
            "/api/v1/compare/**",
            "/api/v1/comments/**",
            "/api/v1/review/**",
            "/api/v1/manual-chat/**",
            "/api/v1/manual-chats/**",
            "/api/v1/notifications/**",
            "/api/v1/files/**"
    };

    public static boolean isPublic(String httpMethod, String requestPath) {
        if (requestPath == null) {
            return false;
        }

        if ("OPTIONS".equalsIgnoreCase(httpMethod)) {
            return true;
        }

        if (matches(requestPath, PUBLIC_ENDPOINTS)) {
            return true;
        }

        return "GET".equalsIgnoreCase(httpMethod) && matches(requestPath, PUBLIC_GET_ENDPOINTS);
    }

    private static boolean matches(String path, String[] patterns) {
        return Arrays.stream(patterns)
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }
}
