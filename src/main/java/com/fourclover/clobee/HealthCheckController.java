package com.fourclover.clobee;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
public class HealthCheckController {

    //localhost:8080/api/health-check
    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("✅ Spring Boot is running.");
    }
}