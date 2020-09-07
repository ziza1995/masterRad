package com.tools.edutool.service;

import com.tools.edutool.model.Permission;
import com.tools.edutool.model.Role;
import com.tools.edutool.model.User;
import com.tools.edutool.repository.PermissionRepository;
import com.tools.edutool.repository.RoleRepository;
import com.tools.edutool.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional
                .orElseThrow(() -> new UsernameNotFoundException("No user " +
                        "Found with username : " + username));

        return new org.springframework.security
                .core.userdetails.User(user.getUsername(), user.getPassword(),
                user.isEnabled(), true, true,
                !loginAttemptService.isBlocked(username), getGrantedAuthorities(user));
    }

    private List<GrantedAuthority> getGrantedAuthorities(User user) {
        List<Role> roles = roleRepository.findByUser(user);
        List<Permission> userPermissions = new ArrayList<>();
        for (Role role : roles) {
            List<Permission> rolePermissions = permissionRepository.findByRole(role);
            userPermissions.addAll(rolePermissions);
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Permission permission : userPermissions) {
            grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName()));
        }
        return grantedAuthorities;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return singletonList(new SimpleGrantedAuthority(role));
    }
}