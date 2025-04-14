package com.vn.capstone.domain.response.dtoAuth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}