package com.fourclover.clobee.batch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/batch")
public class BatchController {
    @Value("${batch.secret}")
    private String secretKey;

    // 매일 새벽 1시 00분 실행
    @Scheduled(cron = "0 0 1 * * *")
    @PostMapping("/cardEvent")
    public ResponseEntity<String> runCardEventInfoBatch(@RequestHeader("X-Secret-Key") String secret) throws Exception {
        if (!secret.equals(secretKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Secret Key");
        }

        return ResponseEntity.ok("Batch Started");
    }
}
