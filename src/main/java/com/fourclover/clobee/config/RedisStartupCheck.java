package com.fourclover.clobee.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RedisStartupCheck {
    @Bean
    public CommandLineRunner redisPingCheck(RedisConnectionFactory factory) {
        return args -> {
            try (var conn = factory.getConnection()) {
                String pong = conn.ping();
                System.out.println("▶▶ Redis PING → " + pong);
            } catch (Exception e) {
                System.err.println("▶▶ Redis 연결 실패!");
                e.printStackTrace();
            }
        };
    }
}

