package com.jyoxin.smartshop.service.impl;

import com.jyoxin.smartshop.constant.SessionConstants;
import com.jyoxin.smartshop.entity.User;
import com.jyoxin.smartshop.entity.enums.Role;
import com.jyoxin.smartshop.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class SessionServiceImpl implements SessionService {


    @Override
    public void createSession(HttpSession session, User user) {
        session.setAttribute(SessionConstants.USER_ID, user.getId());
        session.setAttribute(SessionConstants.USERNAME, user.getUsername());
        session.setAttribute(SessionConstants.USER_ROLE, user.getRole().name());
    }

    @Override
    public void invalidateSession(HttpSession session) {
        session.invalidate();
    }

    @Override
    public Optional<Long> getCurrentUserId(HttpSession session) {
        Object userIdObj = session.getAttribute(SessionConstants.USER_ID);

        if (userIdObj instanceof Long userId) {
            return Optional.of(userId);
        }

        if (userIdObj instanceof Integer userId) {
            return Optional.of(userId.longValue());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Role> getCurrentUserRole(HttpSession session) {
        Object roleStringObj = session.getAttribute(SessionConstants.USER_ROLE);

        if (roleStringObj instanceof String roleString) {
            try {
                return Optional.of(Role.valueOf(roleString));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid session role attribute '{}' found for key: {}", roleString, SessionConstants.USER_ROLE);
            }
        }
        return Optional.empty();
    }


    @Override
    public boolean isAuthenticated(HttpSession session) {
        return getCurrentUserId(session).isPresent();
    }
}