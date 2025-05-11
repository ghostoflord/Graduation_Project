package com.vn.capstone.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vn.capstone.domain.User;
import com.vn.capstone.domain.response.ResLoginDTO;
import com.vn.capstone.repository.UserRepository;
import com.vn.capstone.util.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, @Lazy SecurityUtil securityUtil) {
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Debug attributes nếu cần
        System.out.println("OAuth2 attributes: " + attributes);

        // Lấy email từ provider (tuỳ từng provider sẽ khác)
        String email = extractEmail(attributes);
        if (email == null || email.isEmpty()) {
            String errorMessage = URLEncoder.encode("Không lấy được email từ provider", StandardCharsets.UTF_8);
            response.sendRedirect("http://localhost:3000/login?error=" + errorMessage);
            return;
        }

        // Tìm user trong DB
        User user = userRepository.findByEmail(email);
        if (user == null) {
            String errorMessage = URLEncoder.encode("Người dùng không tồn tại", StandardCharsets.UTF_8);
            response.sendRedirect("http://localhost:3000/login?error=" + errorMessage);
            return;
        }

        // Chuẩn bị DTO
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        userLogin.setId(user.getId());
        userLogin.setEmail(user.getEmail());
        userLogin.setName(user.getName());
        userLogin.setRole(user.getRole() != null ? user.getRole().getName() : null);
        userLogin.setActive(user.isActivate());
        userLogin.setAvatar(user.getAvatar());

        ResLoginDTO dto = new ResLoginDTO();
        dto.setUser(userLogin);

        // Tạo token
        String accessToken = securityUtil.createAccessToken(email, dto);
        String refreshToken = securityUtil.createRefreshToken(email, dto);
        dto.setAccessToken(accessToken);

        // Lưu refresh token
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // Redirect về FE
        // String redirectUri = "http://localhost:3000/oauth2/redirect"
        // + "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
        // + "&user="
        // + URLEncoder.encode(new ObjectMapper().writeValueAsString(userLogin),
        // StandardCharsets.UTF_8);
        String redirectUri = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("accessToken", URLEncoder.encode(accessToken, StandardCharsets.UTF_8))
                .queryParam("user",
                        URLEncoder.encode(new ObjectMapper().writeValueAsString(userLogin), StandardCharsets.UTF_8))
                .toUriString();
        response.sendRedirect(redirectUri);
    }

    /**
     * Trích xuất email an toàn từ attributes (tùy provider mà key có thể khác)
     */
    private String extractEmail(Map<String, Object> attributes) {
        // Kiểm tra trực tiếp trường "email"
        if (attributes.containsKey("email")) {
            return (String) attributes.get("email");
        }

        // Một số provider như GitHub có thể trả trong list "emails"
        if (attributes.containsKey("emails")) {
            Object emails = attributes.get("emails");
            if (emails instanceof Iterable) {
                for (Object emailObj : (Iterable<?>) emails) {
                    if (emailObj instanceof Map) {
                        Object email = ((Map<?, ?>) emailObj).get("email");
                        if (email != null) {
                            return email.toString();
                        }
                    }
                }
            }
        }

        // Nếu không có email, trả về null
        return null;
    }
}
