package com.example.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ProductNotFoundException.class,
            TransactionNotFoundException.class,
            InsufficientStockException.class,
            ProductNotRefundableException.class,
            ResourceNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleCustomExceptions(RuntimeException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Something went wrong");

        return ResponseEntity.internalServerError().body(response);
    }
}
