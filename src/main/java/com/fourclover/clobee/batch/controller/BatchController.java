package com.fourclover.clobee.batch.controller;

import com.fourclover.clobee.batch.service.BatchService;
import com.fourclover.clobee.config.EnvLoader;
import com.fourclover.clobee.config.exception.ApiException;
import com.fourclover.clobee.config.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
public class BatchController {
    private static final Logger log = LoggerFactory.getLogger(BatchController.class);

    private final BatchService batchService;

    @Value("${spring.batch.secret}")
    private String secretKey;

    // 매일 새벽 1시 00분 실행
    @Scheduled(cron = "0 0 1 * * *")
    public ResponseEntity<String> runCardEventInfoBatch() throws Exception {
        return ResponseEntity.ok(batchService.getCardEventsInfo().toString());
    }

    @PostMapping("/cardEvent")
    public ResponseEntity<String> CardEventInfoBatch(@RequestHeader("X-Secret-Key") String secret) throws Exception {
        // log.warn("secret key : {} {}", secretKey, secret);
        if (!secret.equals(secretKey)) {
            throw new ApiException(ErrorCode.SECRET_KEY_NOT_FOUND);
        }
        return ResponseEntity.ok(batchService.getCardEventsInfo().toString());
    }
}
