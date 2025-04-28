package com.fourclover.clobee.event.controller;

import com.fourclover.clobee.event.domain.EventInfo;
import com.fourclover.clobee.event.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    private EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/getCardEvent")
    public ResponseEntity<List<EventInfo>> getCardEvent() {
        return ResponseEntity.ok(eventService.getTotalAttend());
    }
}
