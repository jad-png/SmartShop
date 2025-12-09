package com.jyoxin.smartshop.controller;

import com.jyoxin.smartshop.dto.request.LoginRequest;
import com.jyoxin.smartshop.dto.response.UserDTO;
import com.jyoxin.smartshop.entity.enums.Role;
import com.jyoxin.smartshop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        authController = new AuthController(authService);
    }

    @Test
    @DisplayName("Should return user DTO on successful login")
    void shouldReturnUserDtoOnLogin() {
        LoginRequest request = new LoginRequest("taha", "taha");
        HttpSession session = mock(HttpSession.class);
        UserDTO userDTO = new UserDTO(1L, "taha", Role.ADMIN.name(), 2L, "taha");

        when(authService.login(request, session)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = authController.login(request, session);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(userDTO);
        verify(authService).login(request, session);
    }

    @Test
    @DisplayName("Should logout and return no content")
    void shouldLogoutSuccessfully() {
        HttpSession session = mock(HttpSession.class);

        ResponseEntity<Void> response = authController.logout(session);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();
        verify(authService).logout(session);
    }

    @Test
    @DisplayName("Should return current user info")
    void shouldReturnCurrentUser() {
        HttpSession session = mock(HttpSession.class);
        UserDTO userDTO = new UserDTO(1L, "taha", Role.CLIENT.name(), 2L, "taha");
        when(authService.getCurrentUser(session)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = authController.getCurrentUser(session);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(userDTO);
        verify(authService).getCurrentUser(session);
    }
}
