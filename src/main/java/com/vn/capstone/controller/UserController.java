package com.vn.capstone.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import com.vn.capstone.domain.User;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.service.UserService;
import com.vn.capstone.util.annotation.ApiMessage;
import com.vn.capstone.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    @ApiMessage("fetch all user")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(
                this.userService.fetchAllUser(spec, pageable));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id, String email) {
        User fetchUser = this.userService.fetchUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(fetchUser);
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<User> createUser(@Valid @RequestBody User takeUser)
            throws IdInvalidException {

        boolean isEmailExists = this.userService.isEmailExists(takeUser.getEmail());
        if (isEmailExists) {
            throw new IdInvalidException(
                    "Email" + takeUser.getEmail() + "already exists, please use another email.");
        }
        String hashPassword = this.passwordEncoder.encode(takeUser.getPassword());
        takeUser.setPassword(hashPassword);
        User pressUser = this.userService.handleCreateUser(takeUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(pressUser);
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
