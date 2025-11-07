package com.vn.capstone.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vn.capstone.domain.Cart;
import com.vn.capstone.domain.Comment;
import com.vn.capstone.domain.Like;
import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.Review;
import com.vn.capstone.domain.Role;
import com.vn.capstone.domain.User;
import com.vn.capstone.domain.VerificationToken;
import com.vn.capstone.domain.response.ResCreateUserDTO;
import com.vn.capstone.domain.response.ResUpdateUserDTO;
import com.vn.capstone.domain.response.ResUserDTO;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.repository.CartRepository;
import com.vn.capstone.repository.CommentRepository;
import com.vn.capstone.repository.LikeRepository;
import com.vn.capstone.repository.OrderRepository;
import com.vn.capstone.repository.ReviewRepository;
import com.vn.capstone.repository.UserRepository;
import com.vn.capstone.repository.VerificationTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RoleService roleService;

    private final JavaMailSender mailSender;

    private final PasswordEncoder passwordEncoder;

    private final OrderRepository orderRepository;

    private final CommentRepository commentRepository;

    private final ReviewRepository reviewRepository;

    private final LikeRepository likeRepository;

    private final CartRepository cartRepository;

    public UserService(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository,
            RoleService roleService, OrderRepository orderRepository, CommentRepository commentRepository,
            ReviewRepository reviewRepository,
            LikeRepository likeRepository, CartRepository cartRepository, JavaMailSender mailSender,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.roleService = roleService;
        this.orderRepository = orderRepository;
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
        this.likeRepository = likeRepository;
        this.cartRepository = cartRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
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
            currentUser.setName(reqUser.getName());
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAge(reqUser.getAge());
            // check role
            if (reqUser.getRole() != null) {
                Role r = this.roleService.fetchRoleById(reqUser.getRole().getId());
                currentUser.setRole(r != null ? r : null);
            }
            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User handleCreateUser(User user) {
        // check role
        if (user.getRole() != null) {
            Role r = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(r != null ? r : null);
        }
        return this.userRepository.save(user);
    }

    @Transactional
    public void safeDeleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // 1. Order
        List<Order> orders = orderRepository.findAllByUserId(userId);
        for (Order order : orders) {
            order.setUser(null);
        }
        orderRepository.saveAll(orders);

        // 2. Cart
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            cart.setUser(null);
            cartRepository.save(cart);
        }

        // 3. Likes
        List<Like> likes = likeRepository.findAllByUserId(userId);
        for (Like like : likes) {
            like.setUser(null);
        }
        likeRepository.saveAll(likes);

        // 4. Reviews
        List<Review> reviews = reviewRepository.findAllByUserId(userId);
        for (Review review : reviews) {
            review.setUser(null);
        }
        reviewRepository.saveAll(reviews);

        // 5. Comments
        List<Comment> comments = commentRepository.findAllByUserId(userId);
        for (Comment comment : comments) {
            comment.setUser(null);
        }
        commentRepository.saveAll(comments);

        // 6. Cuối cùng, xóa User
        userRepository.delete(user);
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
        res.setActivate(user.isActivate());
        res.setAddress(user.getAddress());
        res.setAvatar(user.getAvatar());
        res.setRole(user.getRole() != null ? user.getRole().getName() : "NO_ROLE");
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

    public User incrementFailedLoginAttempts(User user) {
        if (user == null) {
            return null;
        }
        int newAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newAttempts);
        if (newAttempts >= MAX_FAILED_LOGIN_ATTEMPTS) {
            user.setAccountLocked(true);
        }
        return userRepository.save(user);
    }

    public User resetFailedLoginAttempts(User user) {
        if (user == null) {
            return null;
        }
        if (user.getFailedLoginAttempts() == 0 && !user.isAccountLocked()) {
            return user;
        }
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        return userRepository.save(user);
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

    public void resetPassword(String otp, String newPassword) {
        User user = userRepository.findByResetPasswordToken(otp)
                .orElseThrow(() -> new RuntimeException("Mã OTP không hợp lệ."));

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null); // Xóa OTP sau khi dùng
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
    }

    // upload avt user
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
