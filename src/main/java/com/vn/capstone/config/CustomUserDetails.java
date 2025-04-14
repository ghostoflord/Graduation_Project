package com.vn.capstone.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.vn.capstone.domain.User;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActivate(); // kiểm tra tài khoản active
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // bạn có thể return roles ở đây nếu muốn
    }

    // Thêm phương thức này nếu bạn cần truy cập đối tượng User đầy đủ
    public User getUser() {
        return user;
    }

}
