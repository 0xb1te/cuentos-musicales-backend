package com.storyteller.platform.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Object> handleError(HttpServletRequest request) {
        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
        
        // Don't handle errors for actuator endpoints - let Spring Boot handle them
        if (requestUri != null && requestUri.startsWith("/actuator")) {
            return ResponseEntity.status(404).body("Not Found");
        }
        
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        
        String errorMessage = exception != null ? exception.getMessage() : "An error occurred";
        
        return ResponseEntity
            .status(statusCode != null ? statusCode : 500)
            .body(new ErrorResponse(statusCode != null ? statusCode : 500, errorMessage));
    }

    private record ErrorResponse(int status, String message) {}
} 