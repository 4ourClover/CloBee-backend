package com.fourclover.clobee.batch.service;

import reactor.core.publisher.Mono;

public interface BatchService {
    public Mono<String> getCardEventsInfo();
    void initCloverInfo();
}
