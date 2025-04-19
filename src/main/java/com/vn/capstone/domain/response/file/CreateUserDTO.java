package com.vn.capstone.domain.response.file;

import org.springframework.web.multipart.MultipartFile;
import com.vn.capstone.util.constant.GenderEnum;

import jakarta.persistence.Column;
import lombok.Data;

@Data
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
