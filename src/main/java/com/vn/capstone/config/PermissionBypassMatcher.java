package com.vn.capstone.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * Handles endpoints that should bypass the permission interceptor (but still
 * require authentication).
 */
@Component
@ConfigurationProperties(prefix = "security.permission-exempt")
public class PermissionBypassMatcher {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private List<String> anyMethod = new ArrayList<>();
    private Map<String, List<String>> byMethod = new HashMap<>();

    public List<String> getAnyMethod() {
        return anyMethod;
    }

    public void setAnyMethod(List<String> anyMethod) {
        this.anyMethod = anyMethod;
    }

    public Map<String, List<String>> getByMethod() {
        return byMethod;
    }

    public void setByMethod(Map<String, List<String>> byMethod) {
        this.byMethod = byMethod;
    }

    public boolean shouldBypass(String httpMethod, String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        if (matches(anyMethod, path)) {
            return true;
        }

        if (httpMethod == null) {
            return false;
        }

        List<String> patterns = byMethod.get(httpMethod.toUpperCase(Locale.ROOT));
        return matches(patterns, path);
    }

    private boolean matches(List<String> patterns, String path) {
        return patterns != null && patterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
