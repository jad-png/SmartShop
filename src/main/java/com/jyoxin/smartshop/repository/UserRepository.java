package com.jyoxin.smartshop.repository;

import com.jyoxin.smartshop.entity.User;
import com.jyoxin.smartshop.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndDeletedFalse(String username);

    boolean existsByUsername(String username);

    List<User> findByRole(Role role);
}
