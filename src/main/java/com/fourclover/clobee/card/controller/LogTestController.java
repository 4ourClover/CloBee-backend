package com.fourclover.clobee.card.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogTestController {

    private static final Logger logger = LoggerFactory.getLogger(LogTestController.class);

    @GetMapping("/log-test")
    public String testLog() {
        logger.warn("🔥 This is a WARN level log");
        logger.error("🔥🔥 This is an ERROR level log");
        return "Logs generated";
    }
}