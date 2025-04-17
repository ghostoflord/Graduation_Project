package com.vn.capstone.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.vn.capstone.config.CustomUserDetails;
import com.vn.capstone.domain.response.dtoAuth.ResponseUtils;
import com.vn.capstone.domain.User;
import com.vn.capstone.domain.VerificationToken;
import com.vn.capstone.domain.request.ReqLoginDTO;
import com.vn.capstone.domain.response.ResCreateUserDTO;
import com.vn.capstone.domain.response.ResLoginDTO;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.dtoAuth.ForgotPasswordRequest;
import com.vn.capstone.domain.response.dtoAuth.ResetPasswordRequest;
import com.vn.capstone.domain.response.dtoAuth.VerifyTokenRequest;
import com.vn.capstone.repository.VerificationTokenRepository;
import com.vn.capstone.service.EmailService;
import com.vn.capstone.service.UserService;
import com.vn.capstone.util.SecurityUtil;
import com.vn.capstone.util.annotation.ApiMessage;
import com.vn.capstone.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;
        private final PasswordEncoder passwordEncoder;
        private final EmailService emailService;
        private final VerificationTokenRepository verificationTokenRepository;

        @Value("${ghost.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                        SecurityUtil securityUtil, UserService userService, PasswordEncoder passwordEncoder,
                        EmailService emailService, VerificationTokenRepository verificationTokenRepository) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
                this.passwordEncoder = passwordEncoder;
                this.emailService = emailService;
                this.verificationTokenRepository = verificationTokenRepository;
        }

        @PostMapping("/auth/login")
        public ResponseEntity<RestResponse<ResLoginDTO>> login(@Valid @RequestBody ReqLoginDTO loginDto) {
                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDto.getUsername(), loginDto.getPassword());

                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                // Kiểm tra tài khoản có active chưa
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                if (!userDetails.isEnabled()) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản chưa được kích hoạt.");
                }

                // set thông tin người dùng đăng nhập vào context (có thể sử dụng sau này)
                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResLoginDTO res = new ResLoginDTO();
                User currentUserDB = this.userService.handleGetUserByUsername(loginDto.getUsername());
                if (currentUserDB != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                        currentUserDB.getId(),
                                        currentUserDB.getEmail(),
                                        currentUserDB.getName());
                        res.setUser(userLogin);
                }

                // create access token
                String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
                res.setAccessToken(access_token);

                // create refresh token
                String refresh_token = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);

                // update user
                this.userService.updateUserToken(refresh_token, loginDto.getUsername());

                // set cookies
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                RestResponse<ResLoginDTO> restResponse = new RestResponse<>();
                restResponse.setStatusCode(HttpStatus.OK.value());
                restResponse.setData(res);
                restResponse.setMessage("Đăng nhập thành công");

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(restResponse);
        }

        @GetMapping("/auth/account")
        @ApiMessage("fetch account")
        public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                User currentUserDB = this.userService.handleGetUserByUsername(email);
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
                ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

                if (currentUserDB != null) {
                        userLogin.setId(currentUserDB.getId());
                        userLogin.setEmail(currentUserDB.getEmail());
                        userLogin.setName(currentUserDB.getName());
                        userGetAccount.setUser(userLogin);
                }

                return ResponseEntity.ok().body(userGetAccount);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("Get User by refresh token")
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
                        throws IdInvalidException {
                if (refresh_token.equals("abc")) {
                        throw new IdInvalidException("Bạn không có refresh token ở cookie");
                }
                // check valid
                Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
                String email = decodedToken.getSubject();

                // check user by token + email
                User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
                if (currentUser == null) {
                        throw new IdInvalidException("Refresh Token không hợp lệ");
                }

                // issue new token/set refresh token as cookies
                ResLoginDTO res = new ResLoginDTO();
                User currentUserDB = this.userService.handleGetUserByUsername(email);
                if (currentUserDB != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                        currentUserDB.getId(),
                                        currentUserDB.getEmail(),
                                        currentUserDB.getName());
                        res.setUser(userLogin);
                }

                // create access token
                String access_token = this.securityUtil.createAccessToken(email, res);
                res.setAccessToken(access_token);

                // create refresh token
                String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

                // update user
                this.userService.updateUserToken(new_refresh_token, email);

                // set cookies
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", new_refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @PostMapping("/auth/logout")
        @ApiMessage("Logout User")
        public ResponseEntity<Void> logout() throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                if (email.equals("")) {
                        throw new IdInvalidException("Access Token không hợp lệ");
                }

                // update refresh token = null
                this.userService.updateUserToken(null, email);

                // remove refresh token cookie
                ResponseCookie deleteSpringCookie = ResponseCookie
                                .from("refresh_token", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                                .body(null);
        }

        @PostMapping("/auth/register")
        @ApiMessage("Register a new user")
        public ResponseEntity<RestResponse<ResCreateUserDTO>> register(@Valid @RequestBody User postManUser)
                        throws IdInvalidException {
                boolean isEmailExist = this.userService.isEmailExists(postManUser.getEmail());
                if (isEmailExist) {
                        throw new IdInvalidException(
                                        "Email " + postManUser.getEmail() + "đã tồn tại, vui lòng sử dụng email khác.");
                }

                String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
                postManUser.setPassword(hashPassword);

                User ericUser = this.userService.handleCreateUser(postManUser);

                // Gán và gửi thông tin kích hoạt
                String token = UUID.randomUUID().toString();
                VerificationToken verificationToken = new VerificationToken();
                verificationToken.setToken(token);
                verificationToken.setUser(postManUser);
                verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

                verificationTokenRepository.save(verificationToken);

                // Gửi email cho người dùng để họ kích hoạt
                emailService.sendVerificationEmail(postManUser.getEmail(), token);
                // Response wrap
                ResCreateUserDTO data = this.userService.convertToResCreateUserDTO(ericUser);
                RestResponse<ResCreateUserDTO> response = new RestResponse<>();
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setMessage("Đăng ký tài khoản thành công, vui lòng kiểm tra email để kích hoạt.");
                response.setData(data);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        // active acc
        @GetMapping("/verify")
        public ResponseEntity<String> verify(@RequestParam String token) {
                boolean verified = userService.verifyUser(token);
                if (verified) {
                        URI redirectUri = URI.create("http://localhost:3000/login");
                        HttpHeaders headers = new HttpHeaders();
                        headers.setLocation(redirectUri);
                        return new ResponseEntity<>(headers, HttpStatus.FOUND);
                } else {
                        URI redirectUri = URI.create("http://localhost:3000/verify-failed");
                        HttpHeaders headers = new HttpHeaders();
                        headers.setLocation(redirectUri);
                        return new ResponseEntity<>(headers, HttpStatus.FOUND);
                }
        }

        // remake
        @PostMapping("/auth/forgot-password")
        public ResponseEntity<RestResponse<Map<String, String>>> forgotPassword(
                        @RequestBody ForgotPasswordRequest request) {
                try {
                        userService.sendPasswordResetEmail(request.getEmail());

                        RestResponse<Map<String, String>> res = new RestResponse<>();
                        res.setStatusCode(HttpStatus.OK.value());
                        res.setMessage("Password reset email sent");

                        Map<String, String> data = new HashMap<>();
                        data.put("email", request.getEmail());
                        res.setData(data);

                        return ResponseEntity.ok(res);

                } catch (RuntimeException e) {
                        RestResponse<Map<String, String>> res = new RestResponse<>();
                        res.setStatusCode(HttpStatus.NOT_FOUND.value());
                        res.setError("Not Found");
                        res.setMessage(e.getMessage());
                        res.setData(null); // hoặc new HashMap<>()
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
                }
        }

        @PostMapping("/auth/verify-reset-token")
        public ResponseEntity<?> verifyResetToken(@RequestBody VerifyTokenRequest request) {
                boolean isValid = userService.validatePasswordResetToken(request.getToken());
                return ResponseEntity.ok(isValid ? "Token valid" : "Invalid token");
        }

        @PostMapping("/auth/reset-password")
        public ResponseEntity<RestResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
                try {
                        userService.resetPassword(request.getToken(), request.getNewPassword());
                        return ResponseEntity.ok(ResponseUtils.success(null, "Password reset successfully"));
                } catch (RuntimeException e) {
                        return ResponseEntity
                                        .status(HttpStatus.BAD_REQUEST)
                                        .body(ResponseUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
                }
        }

}