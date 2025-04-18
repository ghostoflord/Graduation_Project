package com.vn.capstone.controller;

import java.io.IOException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.turkraft.springfilter.boot.Filter;

import com.vn.capstone.domain.User;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.file.CreateUserDTO;
import com.vn.capstone.service.UserService;
import com.vn.capstone.util.annotation.ApiMessage;
import com.vn.capstone.util.error.IdInvalidException;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import java.nio.file.*;
import java.io.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Value("${upload.avatar-dir}")
    private String avatarUploadDir;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    @ApiMessage("fetch all user")
    public ResponseEntity<RestResponse<ResultPaginationDTO>> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        ResultPaginationDTO result = this.userService.fetchAllUser(spec, pageable);

        RestResponse<ResultPaginationDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("fetch all user");
        response.setData(result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id, String email) {
        User fetchUser = this.userService.fetchUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(fetchUser);
    }

    // @PostMapping("/users")
    // @ApiMessage("Create a new user")
    // public ResponseEntity<User> createUser(@Valid @RequestBody User takeUser)
    // throws IdInvalidException {

    // boolean isEmailExists = this.userService.isEmailExists(takeUser.getEmail());
    // if (isEmailExists) {
    // throw new IdInvalidException(
    // "Email" + takeUser.getEmail() + "already exists, please use another email.");
    // }
    // String hashPassword = this.passwordEncoder.encode(takeUser.getPassword());
    // takeUser.setPassword(hashPassword);
    // User pressUser = this.userService.handleCreateUser(takeUser);
    // return ResponseEntity.status(HttpStatus.CREATED).body(pressUser);
    // }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(
            @Valid @ModelAttribute CreateUserDTO userDTO) throws IOException {

        // 1. Kiểm tra email tồn tại
        if (userService.isEmailExists(userDTO.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // 2. Xử lý avatar (nếu có)
        String avatarUrl = null;
        if (userDTO.getAvatar() != null && !userDTO.getAvatar().isEmpty()) {
            avatarUrl = saveAvatar(userDTO.getAvatar());
        }

        // 3. Tạo user mới
        User newUser = new User();
        newUser.setName(userDTO.getUsername());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setAvatar(avatarUrl); // Lưu đường dẫn avatar

        // 4. Lưu vào database
        User savedUser = userService.handleCreateUser(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    private String saveAvatar(MultipartFile avatarFile) throws IOException {
        // Tạo tên file duy nhất
        String fileName = "user_" + System.currentTimeMillis() + ".jpg";

        // Tạo thư mục nếu chưa tồn tại
        Path uploadPath = Paths.get(avatarUploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Lưu file vào thư mục
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName; // Đường dẫn tương đối để lưu DB
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User with id = " + id + " does not exist");
        }

        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) throws IdInvalidException {
        User pressUser = this.userService.handleUpdateUser(user);
        if (pressUser == null) {
            throw new IdInvalidException("User with id = " + user.getId() + " does not exist");
        }
        return ResponseEntity.ok(pressUser);
    }

}
