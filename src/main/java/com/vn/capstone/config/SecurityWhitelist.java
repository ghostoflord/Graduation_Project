package com.vn.capstone.config;

/**
 * Centralized list of endpoints that should remain publicly accessible without
 * authentication and permission checks.
 */
public final class SecurityWhitelist {

    private SecurityWhitelist() {
    }

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
}
