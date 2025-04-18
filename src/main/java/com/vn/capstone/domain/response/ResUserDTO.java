package com.vn.capstone.domain.response;

import java.time.Instant;

import com.vn.capstone.util.constant.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {

    private long id;
    private String email;
    private String name;
    private GenderEnum gender;
    private String address;
    private String age;
    private Instant updatedAt;
    private Instant createdAt;
    private boolean activate;
    private String avatar;
}
