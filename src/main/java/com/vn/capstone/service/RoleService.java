package com.vn.capstone.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vn.capstone.domain.Permission;
import com.vn.capstone.domain.Role;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.repository.PermissionRepository;
import com.vn.capstone.repository.RoleRepository;


@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    // get role by id
    public Role fetchRoleById(long id) {
        Optional<Role> Orole = this.roleRepository.findById(id);
        if (Orole.isPresent()) {
            return Orole.get();
        }
        return null;
    }

    // get all role
    public ResultPaginationDTO fetchAllRole(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageRole.getTotalPages());
        mt.setTotal(pageRole.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageRole.getContent());
        return rs;
    }

    // delete role
    public void handleDeleteRole(long id) {
        this.roleRepository.deleteById(id);
    }

    // post role
    public Role handleCreateRole(Role role) {
        // check permissions
        if (role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(dbPermissions);
        }
        return this.roleRepository.save(role);
    }

    // put role
    public Role handleUpdateRole(Role reqRole) {
        Role roleDB = this.fetchRoleById(reqRole.getId());
        // check permissions
        if (reqRole.getPermissions() != null) {
            List<Long> reqPermissions = reqRole.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            reqRole.setPermissions(dbPermissions);
        }

        roleDB.setName(reqRole.getName());
        roleDB.setDescription(reqRole.getDescription());
        roleDB.setPermissions(reqRole.getPermissions());
        roleDB = this.roleRepository.save(roleDB);
        return roleDB;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

}