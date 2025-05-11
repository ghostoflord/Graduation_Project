package com.vn.capstone.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vn.capstone.domain.User;
import com.vn.capstone.domain.Role;
import com.vn.capstone.repository.RoleRepository;
import com.vn.capstone.repository.UserRepository;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Gọi API mặc định để lấy các thuộc tính cơ bản
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if ("github".equalsIgnoreCase(registrationId)) {
            // Gọi GitHub API để lấy email chính xác
            String email = fetchGithubEmail(userRequest.getAccessToken().getTokenValue());
            if (email != null) {
                attributes.put("email", email);
            }
        }

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "email" // đặt key mặc định để lấy "name" hoặc "email"
        );
    }

    private String fetchGithubEmail(String accessToken) {
        String uri = "https://api.github.com/user/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET, entity, List.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            List<Map<String, Object>> emails = response.getBody();
            if (emails != null) {
                for (Map<String, Object> emailInfo : emails) {
                    Boolean primary = (Boolean) emailInfo.get("primary");
                    Boolean verified = (Boolean) emailInfo.get("verified");
                    if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified)) {
                        return (String) emailInfo.get("email");
                    }
                }
            }
        }

        return null;
    }
}