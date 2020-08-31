package com.tools.edutool.repository;

import com.tools.edutool.model.Role;
import com.tools.edutool.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
     List<Role> findByUser(User user);
}
