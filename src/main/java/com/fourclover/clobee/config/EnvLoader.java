package com.fourclover.clobee.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader implements EnvironmentPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(EnvLoader.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            // 현재 IP 확인
            String hostAddress = InetAddress.getLocalHost().getHostAddress();

            if ("127.0.0.1".equals(hostAddress) || "localhost".equals(hostAddress)) {
                // 기본 : .env.local 로드
                Dotenv dotenv = Dotenv.configure().filename(".env.local").load();
                log.info(".env.local for localhost");

                Map<String, Object> envMap = new HashMap<>();
                dotenv.entries().forEach(entry -> envMap.put(entry.getKey(), entry.getValue()));

                environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envMap));
            }
        } catch (UnknownHostException e) {
            log.error("Error loading environment properties", e);
        }
    }
}