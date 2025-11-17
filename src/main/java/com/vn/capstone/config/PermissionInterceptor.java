package com.vn.capstone.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.vn.capstone.domain.Permission;
import com.vn.capstone.domain.Role;
import com.vn.capstone.domain.User;
import com.vn.capstone.repository.PermissionRepository;
import com.vn.capstone.service.UserService;
import com.vn.capstone.util.SecurityUtil;
import com.vn.capstone.util.error.IdInvalidException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(PermissionInterceptor.class);

    private static final String[] ALWAYS_ALLOWED_ENDPOINTS = {
            "/api/v1/auth/**", // Auth & account lifecycle
            "/api/v1/payment/**", // Payment provider callbacks
            "/api/v1/manual-chat/**", // Legacy chat widget
            "/api/v1/manual-chats/**" // New chat widget namespace
    };

    private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/v1/products/**", // Catalog browsing
            "/api/v1/product-details/**", // SKU detail
            "/api/v1/flash-sales/**", // Flash-sale listings
            "/api/v1/slides/**", // Homepage banners
            "/api/v1/compare/**", // Product comparison
            "/api/v1/reviews/**", // Read reviews
            "/api/v1/comments/**", // Read comments
            "/api/v1/likes/**", // Like counts & listings
            "/api/v1/vouchers/**" // Voucher lookups
    };

    private static final String[] PUBLIC_USER_ENDPOINTS = {
            "/api/v1/carts/**", // Cart CRUD for customers
            "/api/v1/comments", // Create product comments
            "/api/v1/reviews/**", // Write/delete reviews
            "/api/v1/likes/**", // Toggle favourites
            "/api/v1/orders/place", // Checkout order
            "/api/v1/orders/checkout", // Legacy checkout flow
            "/api/v1/orders/{id}/cancel", // Buyer cancels their order
            "/api/v1/orders/my-orders", // Buyer order list
            "/api/v1/orders/{orderId}/details" // Buyer order detail
    };

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Autowired
    UserService userService;

    @Autowired
    PermissionRepository permissionRepository;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        // System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);
        log.debug("Request to save path : {}", path);

        if (isPublicEndpoint(path, httpMethod)) {
            return true;
        }

        if (path == null || httpMethod == null) {
            throw new IdInvalidException("Đường dẫn không xác định, không thể xác thực quyền.");
        }

        if (!permissionRepository.existsByApiPathAndMethod(path, httpMethod)) {
            throw new IdInvalidException("Endpoint chưa được khai báo quyền truy cập.");
        }

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (email != null && !email.isEmpty()) {
            User user = this.userService.handleGetUserByUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    if ("SUPER_ADMIN".equalsIgnoreCase(role.getName())) {
                        return true;
                    }

                    boolean isAllow = permissionRepository.existsByRoles_IdAndApiPathAndMethod(
                            role.getId(),
                            path,
                            httpMethod);

                    if (!isAllow) {
                        throw new IdInvalidException("Bạn không có quyền truy cập endpoint này.");
                    }
                } else {
                    throw new IdInvalidException("Bạn không có quyền truy cập endpoint này.");
                }
            }
        }

        return true;
    }

    private boolean isPublicEndpoint(String path, String method) {
        if (path == null) {
            return true;
        }

        if (matches(path, ALWAYS_ALLOWED_ENDPOINTS) || matches(path, PUBLIC_USER_ENDPOINTS)) {
            return true;
        }

        if (method != null && method.equalsIgnoreCase("GET") && matches(path, PUBLIC_GET_ENDPOINTS)) {
            return true;
        }

        return false;
    }

    private boolean matches(String path, String[] patterns) {
        for (String pattern : patterns) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}