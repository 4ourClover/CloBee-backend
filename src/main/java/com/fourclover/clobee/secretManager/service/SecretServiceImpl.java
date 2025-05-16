package com.fourclover.clobee.secretManager.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Service
@RequiredArgsConstructor
public class SecretServiceImpl implements SecretService {

    private String secretValue;

    @PostConstruct
    public void loadSecrets() {
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.AP_NORTHEAST_2) // 🔁 필요 시 지역 변경
                .build();

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId("your/secret/id") // 🔁 실제 secret 이름
                .build();

        GetSecretValueResponse response = client.getSecretValue(request);
        this.secretValue = response.secretString(); // JSON 형식 또는 단순 문자열
    }

    @Override
    public String getSecret() {
        return this.secretValue;
    }
}
