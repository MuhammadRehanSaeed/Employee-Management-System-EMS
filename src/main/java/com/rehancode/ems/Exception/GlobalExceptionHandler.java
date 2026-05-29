package com.rehancode.ems.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentials.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredential(InvalidCredentials ex) {
        log.warn("Bad credentials: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .data(null)
                .success(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserNotAuthenticated.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotAuthenticated(UserNotAuthenticated ex) {
        log.warn("User Not Authenticated {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .data(null)
                .success(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(CheckInExists.class)
    public ResponseEntity<ApiResponse<Object>> handleCheckInExists(CheckInExists ex) {
        log.warn("CheckIn Exists {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .data(null)
                .success(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .data(null)
                .success(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(UserExistsAlready.class)
    public ResponseEntity<ApiResponse<Object>> handleUserExistsAlready(UserExistsAlready ex) {
        log.warn("User Already Exists {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .data(null)
                .success(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(BadCredentials.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredential(BadCredentials ex) {
        log.warn("Bad credentials: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .data(null)
                .success(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Invalid credentials")
                .data(null)
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(InvalidLeaveException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidLeave(InvalidLeaveException ex) {
        log.warn("Invalid leave request: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .data(null)
                .success(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidDate(HttpMessageNotReadableException ex) {
        log.warn("Unreadable HTTP message: {}", ex.getMessage());

        String message = "Invalid date format or invalid date provided";

        Throwable cause = ex.getCause();

        if (cause != null && cause.getMessage() != null) {
            message = cause.getMessage();
        }

        ApiResponse<String> response = ApiResponse.<String>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .data(null)
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotExists.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UserNotExists ex) {
        log.warn("User not found: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .data(null)
                .success(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(org.springframework.security.authentication.LockedException.class)
    public ResponseEntity<ApiResponse<Object>> handleSpringLockedException(
            org.springframework.security.authentication.LockedException ex) {

        log.warn("Locked account access attempt: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("User account is locked. Contact administrator.")
                .data(null)
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse<Object>> handleLockedException(AccountLockedException ex) {
        log.warn("Locked account access attempt: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .data(null)
                .success(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        log.warn("Validation failed: {}", errors);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .success(false)
                .message("Validation Failed")
                .data(errors)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .data(null)
                .success(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
