package com.fourclover.clobee.batch.service;

import com.fourclover.clobee.event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class BatchServiceImpl implements BatchService {

    @Autowired
    private EventService eventService;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8000")
            .build();

    public Mono<String> getCardEventsInfo() {
        return webClient.get()
                .uri("/cardEventsInfo")
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public void initCloverInfo() {
        eventService.initCloverGame();
    }
}
