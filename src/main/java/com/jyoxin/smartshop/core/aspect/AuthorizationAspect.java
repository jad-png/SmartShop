package com.jyoxin.smartshop.core.aspect;

import com.jyoxin.smartshop.core.annotation.HasRole;
import com.jyoxin.smartshop.core.exception.ForbiddenException;
import com.jyoxin.smartshop.core.exception.UnauthorizedException;
import com.jyoxin.smartshop.entity.enums.Role;
import com.jyoxin.smartshop.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@Order(2)
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final SessionService sessionService;
    private final HttpServletRequest request;

    @Before("@annotation(com.jyoxin.smartshop.core.annotation.HasRole) || @within(com.jyoxin.smartshop.core.annotation.HasRole)")
    public void checkAuthorization(JoinPoint joinPoint) {
        HttpSession session = request.getSession(false);

        if (session == null || !sessionService.isAuthenticated(session)) {
            throw new UnauthorizedException("User must be logged in to access this resource");
        }

        Role userRole = sessionService.getCurrentUserRole(session)
                .orElseThrow(() -> new ForbiddenException("User has no role assigned"));

        Role[] allowedRoles = getAllowedRoles(joinPoint);

        boolean isAuthorized = Arrays.asList(allowedRoles).contains(userRole);

        if (!isAuthorized) {
            throw new ForbiddenException("User does not have permission to access this resource");
        }
    }

    private Role[] getAllowedRoles(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        HasRole annotation = method.getAnnotation(HasRole.class);

        if (annotation == null) {
            annotation = joinPoint.getTarget().getClass().getAnnotation(HasRole.class);
        }

        return (annotation != null) ? annotation.value() : new Role[]{};
    }
}
