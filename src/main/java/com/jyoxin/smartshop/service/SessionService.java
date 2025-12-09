package com.jyoxin.smartshop.service;

import com.jyoxin.smartshop.entity.User;
import com.jyoxin.smartshop.entity.enums.Role;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public interface SessionService {
    void createSession(HttpSession session, User user);

    void invalidateSession(HttpSession session);

    Optional<Long> getCurrentUserId(HttpSession session);

    Optional<Role> getCurrentUserRole(HttpSession session);

    boolean isAuthenticated(HttpSession session);
}
