package com.fourclover.clobee.config.dbConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories // Redis Repository를 자동으로 스캔하고, Redis를 활용해 데이터 처리가 가능하도록 함
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private RedisPassword password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);
        redisConfiguration.setPort(redisPort);
        redisConfiguration.setPassword(password);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    // RedisRepository 대신 RedisTemplate 를 사용한 이유
    // serializer를 활용해 데이터 형식 커스텀 가능
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // 데이터 key 타입을 문자열으로 커스텀
        redisTemplate.setValueSerializer(new StringRedisSerializer()); // 데이터 value 타입을 문자열으로 커스텀
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}



