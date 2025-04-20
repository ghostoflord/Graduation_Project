package com.vn.capstone.controller;

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
import java.util.Base64;
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
    public ResponseEntity<RestResponse<User>> createUser(@RequestBody CreateUserDTO userDTO) throws IOException {

        // Kiểm tra email đã tồn tại hay chưa
        if (userService.isEmailExists(userDTO.getEmail())) {
            RestResponse<User> errorResponse = new RestResponse<>();
            errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError("Email đã tồn tại");
            errorResponse.setMessage("Không thể tạo tài khoản với email đã tồn tại");
            errorResponse.setData(null);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Kiểm tra password không rỗng
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            RestResponse<User> errorResponse = new RestResponse<>();
            errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError("Password không được để trống hoặc null");
            errorResponse.setMessage("Password không được để trống hoặc null");
            errorResponse.setData(null);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Xử lý avatar nếu có
        String avatarUrl = null;
        if (userDTO.getAvatar() != null && !userDTO.getAvatar().isEmpty()) {
            avatarUrl = saveAvatar(userDTO.getAvatar()); // Gọi method saveAvatar để lưu file
        }

        // Tạo người dùng mới
        User newUser = new User();
        newUser.setName(userDTO.getName());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(userDTO.getPassword()); // Mã hóa password nếu cần thiết
        newUser.setAvatar(avatarUrl); // Set avatar là đường dẫn tới ảnh đã lưu
        newUser.setGender(userDTO.getGender());
        newUser.setAddress(userDTO.getAddress());
        newUser.setAge(userDTO.getAge());

        // Lưu người dùng vào database
        User savedUser = userService.handleCreateUser(newUser);

        // Trả về phản hồi thành công
        RestResponse<User> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setError(null);
        response.setMessage("Tạo người dùng thành công");
        response.setData(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Method để lưu avatar
    public String saveAvatar(String avatarBase64) throws IOException {
        if (avatarBase64 == null || avatarBase64.trim().isEmpty()) {
            return null;
        }

        // Xử lý base64 nếu có prefix như "data:image/jpeg;base64,..."
        String[] parts = avatarBase64.split(",");
        String imageData = parts.length > 1 ? parts[1] : parts[0];

        byte[] decodedImg = java.util.Base64.getDecoder().decode(imageData);

        // Tạo tên file duy nhất
        String fileName = "user_" + System.currentTimeMillis() + ".jpg";
        String avatarUploadDir = "path_to_your_directory"; // Cập nhật với đường dẫn thư mục tải lên

        // Đảm bảo thư mục tồn tại
        java.io.File uploadDir = new java.io.File(avatarUploadDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Tạo đường dẫn đầy đủ đến file
        String fullPath = avatarUploadDir + java.io.File.separator + fileName;

        // Ghi file ra ổ cứng
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fullPath)) {
            fos.write(decodedImg);
        }

        // Trả về tên file (hoặc đường dẫn tương đối nếu cần)
        return fileName;
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<RestResponse<Void>> deleteUser(@PathVariable("id") long id) {
        User currentUser = this.userService.fetchUserById(id);

        RestResponse<Void> response = new RestResponse<>();

        if (currentUser == null) {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setError("User not found");
            response.setMessage("User with id = " + id + " does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        this.userService.handleDeleteUser(id);

        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("User deleted successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/users")
    public ResponseEntity<RestResponse<User>> updateUser(@Valid @RequestBody User user) {
        User updatedUser = this.userService.handleUpdateUser(user);

        RestResponse<User> response = new RestResponse<>();

        if (updatedUser == null) {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setError("User not found");
            response.setMessage("User with id = " + user.getId() + " does not exist");
            response.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setStatusCode(HttpStatus.OK.value());
        response.setError(null);
        response.setMessage("User updated successfully");
        response.setData(updatedUser);

        return ResponseEntity.ok(response);
    }

}
