package com.arquiweb.catalog.events.service;

import com.arquiweb.catalog.events.dto.request.CreateEventRequest;
import com.arquiweb.catalog.events.repository.EventRepository;
import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import com.arquiweb.catalog.venues.domain.Venue;
import com.arquiweb.catalog.venues.dto.response.VenueResponse;
import com.arquiweb.catalog.venues.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final VenueService venueService;

    public String createEvent(CreateEventRequest request) {
        VenueResponse venue = venueService.findVenueById(request.venueId());


    }

}
