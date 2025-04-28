package com.fourclover.clobee.config;

import com.fourclover.clobee.config.exception.GlobalExceptionHandler;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class EnvConfig {
    private static final Logger log = LoggerFactory.getLogger(EnvConfig.class);

    @PostConstruct
    public static void loadEnv() throws UnknownHostException {
        // 현재 IP 확인
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        // 기본 : .env.local 로드
        Dotenv dotenv = Dotenv.configure().filename(".env.local").load();

        if ("127.0.0.1".equals(hostAddress) || "localhost".equals(hostAddress)) {
            log.info(".env.local for localhost");
        } else {
            // Dotenv dotenv = Dotenv.configure().filename(".env.prod").load();
            log.info(".env file for IP: " + hostAddress);
        }

        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
    }
}
