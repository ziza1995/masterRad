package com.tools.edutool.config;

import com.tools.edutool.model.Permission;
import com.tools.edutool.model.Role;
import com.tools.edutool.model.User;
import com.tools.edutool.repository.PermissionRepository;
import com.tools.edutool.repository.RoleRepository;
import com.tools.edutool.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataLoader implements ApplicationRunner {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;
    private PasswordEncoder passwordEncoder;

    public void run(ApplicationArguments args) {
        User first = new User().builder().userId(1L).username("ziza").password(passwordEncoder.encode("markovic")).email("a@b").enabled(true).build();
        userRepository.save(first);

        Role role = new Role().builder().id(1).user(first).name("admin").build();
        roleRepository.save(role);

        Permission permission = new Permission().builder().id(1).name("USER").role(role).build();
        permissionRepository.save(permission);
    }
}
