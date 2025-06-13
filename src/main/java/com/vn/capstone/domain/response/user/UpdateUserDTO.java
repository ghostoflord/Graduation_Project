package com.vn.capstone.domain.response.user;

import com.vn.capstone.util.constant.GenderEnum;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private Long id;
    private String name;
    private GenderEnum gender;
    private String address;
    private String age;
    private String avatar; // base64 nếu có
    private Long roleId;
}
