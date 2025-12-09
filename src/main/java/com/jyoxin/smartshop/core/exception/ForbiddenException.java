package com.jyoxin.smartshop.core.exception;

import com.jyoxin.smartshop.entity.enums.Role;

import java.util.Arrays;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Access denied. You don't have permission to access this resource.");
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(Role currentRole, Role... requiredRoles) {
        super(String.format("Access denied. Required role(s): %s, but you have: %s",
                Arrays.toString(requiredRoles), currentRole));
    }
}