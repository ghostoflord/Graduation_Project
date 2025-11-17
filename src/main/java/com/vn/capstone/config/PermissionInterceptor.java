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
            "/api/v1/auth/**",
            "/api/v1/payment/**",
            "/api/v1/manual-chat/**",
            "/api/v1/manual-chats/**"
    };

    private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/v1/products/**",
            "/api/v1/product-details/**",
            "/api/v1/flash-sales/**",
            "/api/v1/slides/**",
            "/api/v1/compare/**",
            "/api/v1/reviews/**",
            "/api/v1/comments/**",
            "/api/v1/likes/**",
            "/api/v1/vouchers/**"
    };

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Autowired
    UserService userService;

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

                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream().anyMatch(item -> item.getApiPath().equals(path)
                            && item.getMethod().equals(httpMethod));

                    if (isAllow == false) {
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

        for (String pattern : ALWAYS_ALLOWED_ENDPOINTS) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }

        if (method != null && method.equalsIgnoreCase("GET")) {
            for (String pattern : PUBLIC_GET_ENDPOINTS) {
                if (PATH_MATCHER.match(pattern, path)) {
                    return true;
                }
            }
        }

        return false;
    }
}