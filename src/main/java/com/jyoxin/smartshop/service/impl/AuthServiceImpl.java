package com.jyoxin.smartshop.service.impl;

import com.jyoxin.smartshop.core.exception.ResourceNotFoundException;
import com.jyoxin.smartshop.core.exception.UnauthorizedException;
import com.jyoxin.smartshop.dto.request.LoginRequest;
import com.jyoxin.smartshop.dto.response.UserDTO;
import com.jyoxin.smartshop.entity.User;
import com.jyoxin.smartshop.mapper.UserMapper;
import com.jyoxin.smartshop.repository.UserRepository;
import com.jyoxin.smartshop.service.AuthService;
import com.jyoxin.smartshop.service.SessionService;
import com.jyoxin.smartshop.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final UserMapper userMapper;

    @Override
    public UserDTO login(LoginRequest request, HttpSession session) {
        User user = userRepository.findByUsernameAndDeletedFalse(request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!PasswordUtil.verify(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        sessionService.createSession(session, user);

        return userMapper.toDTO(user);
    }

    @Override
    public void logout(HttpSession session) {
        sessionService.invalidateSession(session);
    }

    @Override
    public UserDTO getCurrentUser(HttpSession session) {
        Long userId = sessionService.getCurrentUserId(session)
                .orElseThrow(() -> new UnauthorizedException("No active session found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isDeleted()) {
            sessionService.invalidateSession(session);
            throw new UnauthorizedException("User account has been deactivated");
        }

        return userMapper.toDTO(user);
    }
}