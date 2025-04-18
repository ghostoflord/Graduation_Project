package com.vn.capstone.domain.response.file;

import org.springframework.web.multipart.MultipartFile;

import com.vn.capstone.util.constant.GenderEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserDTO {
    private String username;
    private String email;
    private String password;
    private MultipartFile avatar; // File ảnh từ client
    private GenderEnum gender;
}
