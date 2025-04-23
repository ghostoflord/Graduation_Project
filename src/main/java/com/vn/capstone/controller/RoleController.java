package com.vn.capstone.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.vn.capstone.domain.Role;
import com.vn.capstone.domain.User;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.service.RoleService;
import com.vn.capstone.util.annotation.ApiMessage;
import com.vn.capstone.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    @ApiMessage("fetch all Role")
    public ResponseEntity<RestResponse<ResultPaginationDTO>> getAllRole(
            @Filter Specification<Role> spec,
            Pageable pageable) {

        ResultPaginationDTO result = this.roleService.fetchAllRole(spec, pageable);

        RestResponse<ResultPaginationDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("fetch all user");
        response.setData(result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("fetch Role by id")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id, String email) {
        Role fetchRole = this.roleService.fetchRoleById(id);
        return ResponseEntity.status(HttpStatus.OK).body(fetchRole);
    }

    @PostMapping("/roles")
    @ApiMessage("Create a new Role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role takeRole) {
        Role pressRole = this.roleService.handleCreateRole(takeRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(pressRole);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a Role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id)
            throws IdInvalidException {
        Role currentRole = this.roleService.fetchRoleById(id);
        if (currentRole == null) {
            throw new IdInvalidException("Role with id = " + id + " does not exist");
        }

        this.roleService.handleDeleteRole(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/roles")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role Role) throws IdInvalidException {
        Role pressRole = this.roleService.handleUpdateRole(Role);
        if (pressRole == null) {
            throw new IdInvalidException("Role with id = " + Role.getId() + " does not exist");
        }
        return ResponseEntity.ok(pressRole);
    }

}
