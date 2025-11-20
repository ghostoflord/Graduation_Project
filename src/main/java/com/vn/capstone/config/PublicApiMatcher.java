package com.vn.capstone.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * Holds the list of public endpoints (loaded from configuration) and provides
 * helper methods to check if a request should bypass authentication/permission
 * checks.
 */
@Component
@ConfigurationProperties(prefix = "security.public-api")
public class PublicApiMatcher {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Patterns that are public for every HTTP method.
     */
    private List<String> anyMethod = new ArrayList<>();

    /**
     * Method -> patterns map. The key is the HTTP method name (e.g. GET, POST)
     * defined in configuration.
     */
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

    public String[] getAnyMethodPatterns() {
        return anyMethod.toArray(new String[0]);
    }

    public Map<HttpMethod, String[]> getMethodPatternMap() {
        Map<HttpMethod, String[]> result = new HashMap<>();
        for (Entry<String, List<String>> entry : byMethod.entrySet()) {
            HttpMethod method = HttpMethod.resolve(entry.getKey().toUpperCase(Locale.ROOT));
            if (method != null) {
                result.put(method, entry.getValue().toArray(new String[0]));
            }
        }
        return result;
    }

    public boolean isPublic(String httpMethod, String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        if ("OPTIONS".equalsIgnoreCase(httpMethod)) {
            return true;
        }

        if (matches(anyMethod, path)) {
            return true;
        }

        if (httpMethod == null) {
            return false;
        }

        List<String> methodPatterns = byMethod.get(httpMethod.toUpperCase(Locale.ROOT));
        return matches(methodPatterns, path);
    }

    public String[] getAllPatterns() {
        Set<String> patterns = new HashSet<>(anyMethod);
        byMethod.values().forEach(patterns::addAll);
        return patterns.toArray(new String[0]);
    }

    private boolean matches(List<String> patterns, String path) {
        return patterns != null && patterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
