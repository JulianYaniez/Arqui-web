package com.arquiweb.catalog.events.service;

import com.arquiweb.catalog.events.domain.Event;
import com.arquiweb.catalog.events.domain.EventSector;
import com.arquiweb.catalog.events.dto.request.CreateEventRequest;
import com.arquiweb.catalog.events.dto.request.CreateEventSectorRequest;
import com.arquiweb.catalog.events.repository.EventRepository;
import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import com.arquiweb.catalog.venues.dto.response.SectorResponse;
import com.arquiweb.catalog.venues.dto.response.VenueResponse;
import com.arquiweb.catalog.venues.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final VenueService venueService;

    public String createEvent(CreateEventRequest request) {
        VenueResponse venue = venueService.findVenueById(request.venueId());

        Map<String, SectorResponse> venueSectors = venue.sectors().stream()
                .collect(Collectors.toMap(SectorResponse::id, sector -> sector));

        Set<String> processedSectors = new HashSet<>();
        List<EventSector> eventSectors = new ArrayList<>();

        for (CreateEventSectorRequest esr : request.sectors()) {
            String esrId = esr.sectorId();

            if (!processedSectors.add(esrId))
                throw new InvalidInputException("Duplicate sector ID in request: " + esrId, null);

            SectorResponse sector = venueSectors.get(esr.sectorId());
            if (sector == null)
                throw new InvalidInputException("Given sector with ID '" + esrId + "' does not belong to given venue.", null);

            eventSectors.add(new EventSector(sector, esr.price()));
        }

        Event event = request.toDomain(venue, eventSectors);

        return eventRepository.save(event).getId();
    }

}
