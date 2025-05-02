package com.fourclover.clobee.config;

import lombok.Data;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.service.MessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.nurigo.sdk.NurigoApp;

@Data
@Configuration
public class SmsConfig {

    @Value("${sms.provider.api-key}")
    private String apiKey;

    @Value("${sms.provider.api-secret}")
    private String apiSecret;

    @Value("${sms.provider.sender-phone}")
    private String senderPhone;

    // COOL SMS MessageService bean 등록
    @Bean
    public DefaultMessageService messageService() {
        // "https://api.coolsms.co.kr" 엔드포인트로 초기화
        return NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }
}
