package com.vn.capstone.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.vn.capstone.domain.Role;
import com.vn.capstone.domain.User;
import com.vn.capstone.domain.response.ResUserDTO;
import com.vn.capstone.domain.response.ResultPaginationDTO;
import com.vn.capstone.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> fetchAllRole() {
        return this.roleRepository.findAll();
    }

    public Role fetchRoleById(long id) {
        Optional<Role> RoleOptional = this.roleRepository.findById(id);
        if (RoleOptional.isPresent()) {
            return RoleOptional.get();
        }
        return null;
    }

    public Role handleUpdateRole(Role reqRole) {
        Role currentRole = this.fetchRoleById(reqRole.getId());
        if (currentRole != null) {
            currentRole.setName(reqRole.getName());
            currentRole.setDescription(reqRole.getDescription());
            // update
            currentRole = this.roleRepository.save(currentRole);
        }
        return currentRole;
    }

    public Role handleCreateRole(Role Role) {
        return this.roleRepository.save(Role);
    }

    public void handleDeleteRole(long id) {
        this.roleRepository.deleteById(id);
    }
}
