package com.vn.capstone.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vn.capstone.domain.User;
import com.vn.capstone.domain.VerificationToken;
import com.vn.capstone.domain.response.ResCreateUserDTO;
import com.vn.capstone.domain.response.ResUpdateUserDTO;
import com.vn.capstone.domain.response.ResUserDTO;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.repository.UserRepository;
import com.vn.capstone.repository.VerificationTokenRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // Chỉ dùng DTO, không set raw entity
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream()
                .map(this::convertToResUserDTO)
                .collect(Collectors.toList());

        rs.setResult(listUser); // CHỈ set DTO
        return rs;
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setFirstName(reqUser.getFirstName());
            currentUser.setLastName(reqUser.getLastName());
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAge(reqUser.getAge());
            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    // check email
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public boolean verifyUser(String token) {
        VerificationToken vt = verificationTokenRepository.findByToken(token);
        if (vt == null || vt.getExpiryDate().isBefore(LocalDateTime.now()))
            return false;

        User user = vt.getUser();
        user.setActivate(true);
        userRepository.save(user);

        verificationTokenRepository.delete(vt);
        return true;
    }

    // sendPasswordResetEmail
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email);
        // Tạo token (có thể dùng UUID hoặc OTP 6 số)
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1)); // Token hết hạn sau 1 giờ
        userRepository.save(user);

        // Gửi email
        String resetLink = "http://localhost:8080/api/v1/auth/reset-password?token=" + token;
        String emailBody = "Click the link to reset your password: " + resetLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText(emailBody);
        mailSender.send(message);
    }

    public boolean validatePasswordResetToken(String token) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }
        return true;
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null); // Xóa token sau khi dùng
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
    }

}
