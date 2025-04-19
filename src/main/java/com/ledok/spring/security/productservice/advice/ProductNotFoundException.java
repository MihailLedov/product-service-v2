package com.ledok.spring.security.productservice.advice;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
    super(message);}
}
