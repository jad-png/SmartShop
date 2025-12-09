package com.jyoxin.smartshop.core;

import com.jyoxin.smartshop.core.exception.*;
import com.jyoxin.smartshop.core.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
                        ResourceNotFoundException ex, HttpServletRequest request) {
                log.warn("Resource not found: {}", ex.getMessage());
                return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
        }

        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ApiErrorResponse> handleValidationException(
                        ValidationException ex, HttpServletRequest request) {
                log.warn("Validation error: {}", ex.getMessage());
                return buildResponseWithErrors(HttpStatus.BAD_REQUEST, "Validation Error",
                                ex.getMessage(), request, ex.getErrors());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {

                Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                                .collect(Collectors.toMap(
                                                FieldError::getField,
                                                error -> Objects.requireNonNullElse(error.getDefaultMessage(),
                                                                "Invalid value"),
                                                (existing, replacement) -> existing));

                log.warn("Validation failed: {}", fieldErrors);
                return buildResponseWithErrors(HttpStatus.BAD_REQUEST, "Validation Error",
                                "Input validation failed", request, fieldErrors);
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
                        ConstraintViolationException ex, HttpServletRequest request) {

                Map<String, String> fieldErrors = ex.getConstraintViolations().stream()
                                .collect(Collectors.toMap(
                                                violation -> StreamSupport
                                                                .stream(violation.getPropertyPath().spliterator(),
                                                                                false)
                                                                .reduce((first, second) -> second)
                                                                .map(Object::toString)
                                                                .orElse("unknown"),
                                                ConstraintViolation::getMessage,
                                                (existing, replacement) -> existing));

                log.warn("Constraint violation: {}", fieldErrors);
                return buildResponseWithErrors(HttpStatus.BAD_REQUEST, "Validation Error",
                                "Constraint violation", request, fieldErrors);
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ApiErrorResponse> handleUnauthorizedException(
                        UnauthorizedException ex, HttpServletRequest request) {
                log.warn("Unauthorized access: {}", request.getRequestURI());
                return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request);
        }

        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ApiErrorResponse> handleForbiddenException(
                        ForbiddenException ex, HttpServletRequest request) {
                log.warn("Forbidden access: {}", request.getRequestURI());
                return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), request);
        }

        @ExceptionHandler(BusinessRuleException.class)
        public ResponseEntity<ApiErrorResponse> handleBusinessRuleException(
                        BusinessRuleException ex, HttpServletRequest request) {
                log.warn("Business rule violation: {}", ex.getMessage());
                return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Business Rule Violation", ex.getMessage(),
                                request);
        }

        @ExceptionHandler(InsufficientStockException.class)
        public ResponseEntity<ApiErrorResponse> handleInsufficientStockException(
                        InsufficientStockException ex, HttpServletRequest request) {

                log.warn("Insufficient stock: {}", ex.getMessage());

                Map<String, String> stockErrors = Optional.ofNullable(ex.getInsufficientProducts())
                                .orElse(Map.of())
                                .entrySet().stream()
                                .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                entry -> String.format("Requested: %d, Available: %d",
                                                                entry.getValue().requested(),
                                                                entry.getValue().available())));

                return buildResponseWithErrors(HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient Stock",
                                ex.getMessage(), request, stockErrors);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex, HttpServletRequest request) {
                log.warn("Illegal argument: {}", ex.getMessage());
                return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolationException(
                        DataIntegrityViolationException ex, HttpServletRequest request) {
                log.warn("Data integrity violation: {}", ex.getMessage());

                String message = "Database constraint violation";
                if (ex.getMessage() != null) {
                        if (ex.getMessage().contains("unique constraint") || ex.getMessage().contains("Unique")) {
                                message = "A record with this value already exists";
                        } else if (ex.getMessage().contains("foreign key") || ex.getMessage().contains("Foreign")) {
                                message = "Cannot perform operation due to related records";
                        } else if (ex.getMessage().contains("not-null") || ex.getMessage().contains("NULL")) {
                                message = "Required field cannot be null";
                        }
                }

                return buildResponse(HttpStatus.CONFLICT, "Data Integrity Error", message, request);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorResponse> handleGenericException(
                        Exception ex, HttpServletRequest request) {
                log.error("Unexpected error: ", ex);
                return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                                "An unexpected error occurred", request);
        }

        private ResponseEntity<ApiErrorResponse> buildResponse(
                        HttpStatus status, String error, String message, HttpServletRequest request) {

                return ResponseEntity.status(status).body(ApiErrorResponse.of(
                                status.value(),
                                error,
                                message,
                                request.getRequestURI()));
        }

        private ResponseEntity<ApiErrorResponse> buildResponseWithErrors(
                        HttpStatus status, String error, String message, HttpServletRequest request,
                        Map<String, String> errors) {

                return ResponseEntity.status(status).body(ApiErrorResponse.withErrors(
                                status.value(),
                                error,
                                message,
                                request.getRequestURI(),
                                errors));
        }
}