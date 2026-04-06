package com.example.task_manager.adapter.in.web.handler;

import com.example.task_manager.application.exception.TaskNotFoundException;
import com.example.task_manager.domain.exception.TaskAlreadyCompletedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorResponseFactory errorResponseFactory;

    public GlobalExceptionHandler(ErrorResponseFactory errorResponseFactory) {
        this.errorResponseFactory = errorResponseFactory;
    }

    @ExceptionHandler(TaskAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponse> handleTaskAlreadyCompletedException(
            TaskAlreadyCompletedException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;

        return ResponseEntity.status(status)
                .body(errorResponseFactory.build(status, ex.getMessage(), request));
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(
            TaskNotFoundException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status)
                .body(errorResponseFactory.build(status, ex.getMessage(), request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(status)
                .body(errorResponseFactory.build(status, message, request));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(status)
                .body(errorResponseFactory.build(status, message, request));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(errorResponseFactory.build(status, "Malformed or unreadable JSON request body", request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(status)
                .body(errorResponseFactory.build(status, "An unexpected error occurred", request));
    }

}
