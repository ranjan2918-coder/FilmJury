package com.filmjury.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for the entire application.
 * Converts exceptions into meaningful HTTP responses with
 * proper status codes and error messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle resource not found (404).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handle duplicate scorecard submission (400).
     */
    @ExceptionHandler(DuplicateScorecardException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateScorecard(DuplicateScorecardException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handle invalid score values (400).
     */
    @ExceptionHandler(InvalidScoreException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidScore(InvalidScoreException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handle Bean Validation errors from @Valid annotations (400).
     * Collects all field-level validation messages into a single response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errors);
    }

    /**
     * Handle database constraint violations (409 Conflict).
     * Safety net for any remaining foreign key or unique constraint issues.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = "Database constraint violation. ";
        if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")) {
            message += "A record with the same unique value already exists.";
        } else {
            message += "The operation conflicts with existing data.";
        }
        return buildErrorResponse(HttpStatus.CONFLICT, message);
    }

    /**
     * Handle any other unexpected exceptions (500).
     * Re-throws NoResourceFoundException so Spring Boot's default
     * static resource handler can serve HTML/CSS/JS files.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) throws Exception {
        if (ex instanceof NoResourceFoundException) {
            throw ex;
        }
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred: " + ex.getMessage());
    }

    /**
     * Helper method to build a consistent error response body.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
