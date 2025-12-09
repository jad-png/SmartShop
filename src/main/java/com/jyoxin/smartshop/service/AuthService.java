package com.jyoxin.smartshop.service;

import com.jyoxin.smartshop.dto.request.LoginRequest;
import com.jyoxin.smartshop.dto.response.UserDTO;
import jakarta.servlet.http.HttpSession;

public interface AuthService {
    UserDTO login(LoginRequest request, HttpSession session);
    void logout(HttpSession session);
    UserDTO getCurrentUser(HttpSession session);
}