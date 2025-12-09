package com.jyoxin.smartshop.core.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Unauthorized, Login to access this resource");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}