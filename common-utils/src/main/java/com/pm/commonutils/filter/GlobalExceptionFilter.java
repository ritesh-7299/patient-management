package com.pm.commonutils.filter;


import com.pm.commonutils.exceptions.NotFoundException;
import com.pm.commonutils.exceptions.UnauthorizedException;
import com.pm.commonutils.exceptions.ValidationErrorException;
import com.pm.commonutils.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GlobalExceptionFilter {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse<?>> handleNotFoundException(NotFoundException ex) {
        ErrorResponse<?> err = new ErrorResponse<String>(ex.getMessage());
        log.error("not found exception: {}", ex.getMessage());
        return new ResponseEntity<ErrorResponse<?>>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(e ->
                errors.put(e.getField(), e.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(new ErrorResponse<Map<String, String>>(errors));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse<?>> handleUnauthorizedException() {
        return new ResponseEntity<ErrorResponse<?>>(new ErrorResponse<String>("Unauthorized"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity<ErrorResponse<?>> handleValidationException(ValidationErrorException ex) {
        return new ResponseEntity<ErrorResponse<?>>(new ErrorResponse<String>(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse<?>> handleDuplicateException(DataIntegrityViolationException ex) {
        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "Duplicate value found";
        Pattern pattern = Pattern.compile("\\(([^)]+)\\) already exists");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            message = matcher.group(1) + " already exists.";
        }
        return new ResponseEntity<ErrorResponse<?>>(new ErrorResponse<String>(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<?>> handleGeneralError(Exception ex) {
        log.error("\ngeneral error::" + ex);
        return new ResponseEntity<ErrorResponse<?>>(new ErrorResponse<String>("Something went wrong"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
