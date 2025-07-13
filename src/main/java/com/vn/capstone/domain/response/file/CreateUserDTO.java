package com.vn.capstone.domain.response.file;

import com.vn.capstone.util.constant.GenderEnum;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CreateUserDTO {
    private String username;
    private String name;
    private String email;
    private String password;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String avatar;
    private GenderEnum gender;
    private String address;
    private String age;

}
