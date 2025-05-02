package com.fourclover.clobee.batch.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class BatchServiceImpl implements BatchService {
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8000")
            .build();

    public Mono<String> getCardEventsInfo() {
        return webClient.get()
                .uri("/cardEventsInfo")
                .retrieve()
                .bodyToMono(String.class);
    }
}
