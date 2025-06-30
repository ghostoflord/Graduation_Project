package com.vn.capstone.controller;

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
    @ApiMessage("Fetch Role by id")
    public ResponseEntity<RestResponse<Role>> getRoleById(@PathVariable("id") long id) {
        Role role = roleService.fetchRoleById(id);
        RestResponse<Role> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Lấy thông tin role thành công");
        response.setData(role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/roles")
    @ApiMessage("Create a new Role")
    public ResponseEntity<RestResponse<Role>> createRole(@Valid @RequestBody Role takeRole) {
        Role created = roleService.handleCreateRole(takeRole);
        RestResponse<Role> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Tạo role thành công");
        response.setData(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/roles")
    public ResponseEntity<RestResponse<Role>> updateRole(@Valid @RequestBody Role role) throws IdInvalidException {
        Role updated = roleService.handleUpdateRole(role);
        if (updated == null) {
            throw new IdInvalidException("Role với id = " + role.getId() + " không tồn tại");
        }
        RestResponse<Role> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Cập nhật role thành công");
        response.setData(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a Role")
    public ResponseEntity<RestResponse<Void>> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        Role role = roleService.fetchRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role với id = " + id + " không tồn tại");
        }
        roleService.handleDeleteRole(id);

        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Xóa role thành công");
        response.setData(null);
        return ResponseEntity.ok(response);
    }

}
