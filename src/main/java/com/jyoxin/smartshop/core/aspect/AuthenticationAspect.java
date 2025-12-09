package com.jyoxin.smartshop.core.aspect;

import com.jyoxin.smartshop.core.exception.UnauthorizedException;
import com.jyoxin.smartshop.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class AuthenticationAspect {

    private final SessionService sessionService;
    private final HttpServletRequest request;

    @Before("@annotation(com.jyoxin.smartshop.core.annotation.Authenticated) || @within(com.jyoxin.smartshop.core.annotation.Authenticated)")
    public void checkAuthentication() {
        HttpSession session = request.getSession(false);

        if (session == null || !sessionService.isAuthenticated(session)) {
            throw new UnauthorizedException("User is not authenticated");
        }
    }
}