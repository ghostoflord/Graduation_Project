package com.vn.capstone.domain.response.dtoAuth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyTokenRequest {
    private String token;
}
