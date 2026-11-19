package com.arquiweb.ticketing.event.service;

import com.arquiweb.ticketing.event.domain.TicketingEvent;
import com.arquiweb.ticketing.event.repository.TicketingEventRepository;
import com.arquiweb.ticketing.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventQueryService {


    private final TicketingEventRepository ticketingEventRepository;

    public TicketingEvent getById(String eventId) {
        return ticketingEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("No event with given ID."));
    }
}
