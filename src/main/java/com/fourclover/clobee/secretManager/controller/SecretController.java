package com.fourclover.clobee.secretManager.controller;

import com.fourclover.clobee.secretManager.service.SecretService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secret")
@RequiredArgsConstructor
public class SecretController {
    private final SecretService secretService;

    @GetMapping
    public ResponseEntity<String> getFrontendConfig() {
        return ResponseEntity.ok(secretService.getSecret());
    }
}
