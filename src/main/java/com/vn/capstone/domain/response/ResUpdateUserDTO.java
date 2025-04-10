package com.vn.capstone.domain.response;

import java.time.Instant;

import com.vn.capstone.util.constant.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String lastName;
    private GenderEnum gender;
    private String address;
    private String age;
    private Instant updatedAt;
}