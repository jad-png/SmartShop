package com.jyoxin.smartshop.controller;

import com.jyoxin.smartshop.core.annotation.Authenticated;
import com.jyoxin.smartshop.dto.request.LoginRequest;
import com.jyoxin.smartshop.dto.response.UserDTO;
import com.jyoxin.smartshop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        return ResponseEntity.ok(authService.login(request, session));
    }

    @Authenticated
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.noContent().build();
    }

    @Authenticated
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(HttpSession session) {
        return ResponseEntity.ok(authService.getCurrentUser(session));
    }
}