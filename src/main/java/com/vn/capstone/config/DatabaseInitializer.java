package com.vn.capstone.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vn.capstone.domain.Permission;
import com.vn.capstone.domain.Role;
import com.vn.capstone.domain.User;
import com.vn.capstone.repository.PermissionRepository;
import com.vn.capstone.repository.RoleRepository;
import com.vn.capstone.repository.UserRepository;
import com.vn.capstone.util.constant.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            arr.add(new Permission("Create a product", "/api/v1/products", "POST", "PRODUCTS"));
            arr.add(new Permission("Update a product", "/api/v1/products", "PUT", "PRODUCTS"));
            arr.add(new Permission("Delete a product", "/api/v1/products/{id}", "DELETE", "PRODUCTS"));
            arr.add(new Permission("Get a product by id", "/api/v1/products/{id}", "GET", "PRODUCTS"));
            arr.add(new Permission("Get products with pagination", "/api/v1/products", "GET", "PRODUCTS"));

            arr.add(new Permission("Get all order", "/api/v1/orders/all", "GET", "ORDERS"));
            arr.add(new Permission("Update a order", "/api/v1/orders/{id}/update", "PUT", "ORDERS"));
            arr.add(new Permission("Delete a order", "/api/v1/orders/{id}", "DELETE", "ORDERS"));
            arr.add(new Permission("Get order with pagination", "/api/v1/orders/all", "GET", "ORRDERS"));

            arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));

            arr.add(new Permission("Create a product detail", "/api/v1/product-details", "POST", "PRODUCT_DETAIL"));
            arr.add(new Permission("Update a product detail", "/api/v1/product-details/{id}", "PUT", "PRODUCT_DETAIL"));
            arr.add(new Permission("Delete a product detail", "/api/v1/product-detail/{id}", "DELETE",
                    "PRODUCT_DETAIL"));
            arr.add(new Permission("Get a product detail by id", "/api/v1/product-details/{id}", "GET",
                    "PRODUCT_DETAIL"));
            // arr.add(new Permission("Get product detail with pagination",
            // "/api/v1/product-details", "GET",
            // "PRODUCT_DETAIL"));

            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
            arr.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));

            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));

            arr.add(new Permission("Create a flash sale", "/api/v1/flash-sales", "POST", "FLASH_SALE"));
            arr.add(new Permission("Update a subscriber", "/api/v1/flash-sales/{id}", "PUT", "FLASH_SALE"));
            arr.add(new Permission("Delete a subscriber", "/api/v1//flash-sales/{id}", "DELETE", "FLASH_SALE"));
            arr.add(new Permission("Get subscribers with pagination", "/api/v1/flash-sales", "GET", "FLASH_SALE"));

            arr.add(new Permission("Download a file", "/api/v1/files", "POST", "FILES"));
            arr.add(new Permission("Get a file", "/api/v1/files", "GET", "FILES"));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("Admin thì full permissions");
            adminRole.setPermissions(allPermissions);

            this.roleRepository.save(adminRole);
        }

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("hn");
            adminUser.setAge("25");
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setName("I'm super admin");
            adminUser.setActivate(true);
            adminUser.setPassword(this.passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            this.userRepository.save(adminUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> END INIT DATABASE");
    }

}
