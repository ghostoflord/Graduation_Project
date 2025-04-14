package com.vn.capstone.config;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.vn.capstone.service.UserService;

@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {

    private final UserService userService;

    public UserDetailsCustom(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.vn.capstone.domain.User user = this.userService.handleGetUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username/password không hợp lệ");
        }

        // return new User(
        // user.getEmail(),     
        // user.getPassword(),
        // Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        return new CustomUserDetails(user); // Sử dụng CustomUserDetails thay vì User của Spring

    }

}
