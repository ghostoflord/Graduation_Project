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
import org.springframework.web.server.ResponseStatusException;

import com.turkraft.springfilter.boot.Filter;
import com.vn.capstone.domain.Order;
import com.vn.capstone.domain.Product;
import com.vn.capstone.domain.Role;
import com.vn.capstone.domain.User;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.domain.response.file.CreateUserDTO;
import com.vn.capstone.domain.response.user.OrderResponseDTO;
import com.vn.capstone.domain.response.user.UpdateUserDTO;
import com.vn.capstone.domain.response.user.UserAccountInfoDto;
import com.vn.capstone.domain.response.user.UserImportDTO;
import com.vn.capstone.repository.OrderRepository;
import com.vn.capstone.repository.UserRepository;
import com.vn.capstone.service.RoleService;
import com.vn.capstone.service.UserService;
import com.vn.capstone.util.SecurityUtil;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.io.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Value("${upload.avatar-dir}")
    private String avatarUploadDir;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final RoleService roleService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, UserRepository userRepository,
            OrderRepository orderRepository, RoleService roleService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.roleService = roleService;
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

        String avatarUrl = userDTO.getAvatar() != null ? saveAvatar(userDTO.getAvatar()) : null;

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
    private String saveAvatar(String avatarBase64) throws IOException {
        if (avatarBase64 == null || avatarBase64.trim().isEmpty()) {
            return null;
        }

        // Nếu có prefix như "data:image/jpeg;base64,...", thì tách ra
        String base64Image;
        if (avatarBase64.contains(",")) {
            base64Image = avatarBase64.substring(avatarBase64.indexOf(",") + 1);
        } else {
            base64Image = avatarBase64;
        }

        // Decode base64
        byte[] decodedImg = Base64.getDecoder().decode(base64Image);

        // Tạo tên file
        String fileName = "user_" + System.currentTimeMillis() + ".jpg";

        File uploadDir = new File(avatarUploadDir);
        if (!uploadDir.exists())
            uploadDir.mkdirs();

        String fullPath = avatarUploadDir + File.separator + fileName;
        try (FileOutputStream fos = new FileOutputStream(fullPath)) {
            fos.write(decodedImg);
        }

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

    @GetMapping("/users/account/info")
    public ResponseEntity<RestResponse<UserAccountInfoDto>> getAccountInfo() {
        String currentEmail = SecurityUtil.getCurrentUserLogin().orElse(null);
        if (currentEmail == null) {
            RestResponse<UserAccountInfoDto> response = new RestResponse<>();
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            response.setError("Unauthorized");
            response.setMessage("Bạn chưa đăng nhập.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = userRepository.findByEmail(currentEmail);
        if (user == null) {
            RestResponse<UserAccountInfoDto> response = new RestResponse<>();
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setError("Not Found");
            response.setMessage("Không tìm thấy người dùng.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        UserAccountInfoDto dto = new UserAccountInfoDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setOrderCount(user.getOrders() != null ? user.getOrders().size() : 0);
        dto.setCartSum(user.getCart() != null ? user.getCart().getSum() : 0);

        RestResponse<UserAccountInfoDto> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setData(dto);
        response.setMessage("Lấy thông tin tài khoản thành công");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/account/order")
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders() {
        String currentUsername = SecurityUtil.getCurrentUserLogin().orElseThrow();
        User user = userRepository.findByEmail(currentUsername);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<Order> orders = orderRepository.findByUser(user);

        List<OrderResponseDTO> dtos = orders.stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setId(order.getId());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setReceiverName(order.getReceiverName());
            dto.setReceiverPhone(order.getReceiverPhone());
            dto.setReceiverAddress(order.getReceiverAddress());
            dto.setStatus(order.getStatus());
            dto.setCreatedAt(order.getCreatedAt());

            // User info
            dto.setUser(new OrderResponseDTO.UserDTO(
                    order.getUser().getName(), order.getUser().getEmail()));

            // Products in order
            List<OrderResponseDTO.OrderProductDTO> products = order.getOrderDetails().stream().map(detail -> {
                Product product = detail.getProduct();
                return new OrderResponseDTO.OrderProductDTO(
                        product.getName(),
                        product.getImage(), // optional: thêm ảnh đại diện sản phẩm
                        detail.getQuantity(),
                        detail.getPrice());
            }).collect(Collectors.toList());

            dto.setProducts(products);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/users/import")
    public ResponseEntity<RestResponse<Void>> importUsers(@RequestBody List<UserImportDTO> users) {
        for (UserImportDTO dto : users) {
            User user = new User();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setAddress(dto.getAddress());
            userRepository.save(user);
        }

        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Imported successfully");
        response.setData(null);
        response.setError(null);

        return ResponseEntity.ok(response);
    }

    // upload avt when update user
    @PostMapping("/users/update")
    public ResponseEntity<RestResponse<User>> updateUser(@RequestBody UpdateUserDTO userDTO) throws IOException {
        // Tìm user theo id
        User user = userService.findById(userDTO.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy user"));

        // Cập nhật thông tin
        user.setName(userDTO.getName());
        user.setGender(userDTO.getGender());
        user.setAddress(userDTO.getAddress());
        user.setAge(userDTO.getAge());

        // Cập nhật role nếu có
        if (userDTO.getRoleId() != null) {
            Role role = roleService.fetchRoleById(userDTO.getRoleId());
            if (role == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Không tìm thấy role với id: " + userDTO.getRoleId());
            }
            user.setRole(role);
        }

        // Nếu có avatar mới
        if (userDTO.getAvatar() != null && !userDTO.getAvatar().trim().isEmpty()) {
            if (user.getAvatar() != null) {
                File oldAvatar = new File(avatarUploadDir + File.separator + user.getAvatar());
                if (oldAvatar.exists()) {
                    oldAvatar.delete();
                }
            }

            String newAvatarFileName = saveAvatar(userDTO.getAvatar());
            user.setAvatar(newAvatarFileName);
        }

        User updatedUser = userService.handleUpdateUser(user);

        RestResponse<User> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Cập nhật người dùng thành công");
        response.setData(updatedUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/me/update")
    public ResponseEntity<RestResponse<User>> selfUpdateUser(@RequestBody UpdateUserDTO userDTO) throws IOException {
        // Tìm user theo id
        User user = userService.findById(userDTO.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy user"));

        // Cập nhật thông tin
        user.setName(userDTO.getName());
        user.setGender(userDTO.getGender());
        user.setAddress(userDTO.getAddress());
        user.setAge(userDTO.getAge());

        // Cập nhật role nếu có
        if (userDTO.getRoleId() != null) {
            Role role = roleService.fetchRoleById(userDTO.getRoleId());
            if (role == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Không tìm thấy role với id: " + userDTO.getRoleId());
            }
            user.setRole(role);
        }

        // Nếu có avatar mới
        if (userDTO.getAvatar() != null && !userDTO.getAvatar().trim().isEmpty()) {
            if (user.getAvatar() != null) {
                File oldAvatar = new File(avatarUploadDir + File.separator + user.getAvatar());
                if (oldAvatar.exists()) {
                    oldAvatar.delete();
                }
            }

            String newAvatarFileName = saveAvatar(userDTO.getAvatar());
            user.setAvatar(newAvatarFileName);
        }

        User updatedUser = userService.handleUpdateUser(user);

        RestResponse<User> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Cập nhật người dùng thành công");
        response.setData(updatedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/me/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<RestResponse<User>> userTakeProfile(@PathVariable("id") long id) {
        User fetchUser = this.userService.fetchUserById(id);

        RestResponse<User> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setData(fetchUser);
        response.setMessage("Lấy thông tin người dùng thành công");

        return ResponseEntity.ok(response);
    }

}
