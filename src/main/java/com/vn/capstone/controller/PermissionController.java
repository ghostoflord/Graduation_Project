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
import com.vn.capstone.domain.Permission;
import com.vn.capstone.domain.response.RestResponse;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.service.PermissionService;
import com.vn.capstone.util.annotation.ApiMessage;
import com.vn.capstone.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<RestResponse<Permission>> create(@Valid @RequestBody Permission p) throws IdInvalidException {
        // check exist
        if (this.permissionService.isPermissionExist(p)) {
            throw new IdInvalidException("Permission đã tồn tại.");
        }

        Permission created = this.permissionService.create(p);

        RestResponse<Permission> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Tạo permission thành công");
        response.setData(created);
        response.setError(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<RestResponse<Permission>> update(@Valid @RequestBody Permission p) throws IdInvalidException {
        // check exist by id
        if (this.permissionService.fetchById(p.getId()) == null) {
            throw new IdInvalidException("Permission với id = " + p.getId() + " không tồn tại.");
        }

        // check exist by module, apiPath and method
        if (this.permissionService.isPermissionExist(p)) {
            throw new IdInvalidException("Permission đã tồn tại.");
        }

        Permission updated = this.permissionService.update(p);

        RestResponse<Permission> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Cập nhật permission thành công");
        response.setData(updated);
        response.setError(null);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete a permission")
    public ResponseEntity<RestResponse<Void>> delete(@PathVariable("id") long id) throws IdInvalidException {
        // Check exist by id
        if (this.permissionService.fetchById(id) == null) {
            throw new IdInvalidException("Permission với id = " + id + " không tồn tại.");
        }

        this.permissionService.delete(id);

        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Xóa permission thành công");
        response.setData(null); // Không có dữ liệu trả về
        response.setError(null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch permissions")
    public ResponseEntity<RestResponse<ResultPaginationDTO>> getPermissions(
            @Filter Specification<Permission> spec, Pageable pageable) {

        ResultPaginationDTO result = this.permissionService.getPermissions(spec, pageable);

        RestResponse<ResultPaginationDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Fetch permissions successfully");
        response.setData(result);

        return ResponseEntity.ok(response);
    }

}