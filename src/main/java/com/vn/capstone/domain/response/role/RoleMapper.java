package com.vn.capstone.domain.response.role;

import java.util.stream.Collectors;

import com.vn.capstone.domain.Permission;
import com.vn.capstone.domain.Role;
import com.vn.capstone.domain.response.permission.PermissionDTO;

public class RoleMapper {
    public static RoleResponseDTO toDTO(Role role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        dto.setCreatedBy(role.getCreatedBy());
        dto.setUpdatedBy(role.getUpdatedBy());

        if (role.getPermissions() != null) {
            dto.setPermissions(role.getPermissions().stream()
                    .map(RoleMapper::toPermissionDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private static PermissionDTO toPermissionDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setApiPath(permission.getApiPath());
        dto.setMethod(permission.getMethod());
        dto.setModule(permission.getModule());
        return dto;
    }
}
