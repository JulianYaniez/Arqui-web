
package com.arquiweb.ticketing.event.service;

import com.arquiweb.ticketing.event.domain.TicketingEvent;
import com.arquiweb.ticketing.event.domain.exceptions.InvalidEventException;
import com.arquiweb.ticketing.event.repository.TicketingEventRepository;
import com.arquiweb.ticketing.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final TicketingEventRepository ticketingEventRepository;

    public void validateEvent(String eventId) {
        TicketingEvent event = ticketingEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getSalesEnabled()) {
            throw new InvalidEventException(
                    "Given event has already happened or was canceled."
            );
        }
    }
}
