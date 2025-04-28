package com.fourclover.clobee.event.service;

import com.fourclover.clobee.event.domain.EventInfo;
import com.fourclover.clobee.event.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<EventInfo> getTotalAttend() {
        return eventRepository.getTotalAttend();
    }
}
