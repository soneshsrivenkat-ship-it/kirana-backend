package com.example.demo.exception;

public class ProductNotRefundableException extends RuntimeException {
    public ProductNotRefundableException(String message) {
        super(message);
    }
}
