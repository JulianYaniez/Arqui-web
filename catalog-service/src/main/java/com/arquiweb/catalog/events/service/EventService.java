package com.arquiweb.catalog.events.service;

import com.arquiweb.catalog.events.domain.Event;
import com.arquiweb.catalog.events.domain.EventSector;
import com.arquiweb.catalog.events.dto.request.CreateEventRequest;
import com.arquiweb.catalog.events.dto.request.CreateEventSectorRequest;
import com.arquiweb.catalog.events.dto.request.EventCriteriaQuery;
import com.arquiweb.catalog.events.dto.response.EventResponse;
import com.arquiweb.catalog.events.repository.EventCustomRepository;
import com.arquiweb.catalog.events.repository.EventRepository;
import com.arquiweb.catalog.infrastructure.client.TicketingClient;
import com.arquiweb.catalog.infrastructure.client.dtos.command.InitCapacityRequest;
import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import com.arquiweb.catalog.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.catalog.venues.dto.response.SectorResponse;
import com.arquiweb.catalog.venues.dto.response.VenueResponse;
import com.arquiweb.catalog.venues.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventCustomRepository eventCustomRepository;
    private final VenueService venueService;
    private final TicketingClient ticketingClient;

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


    public String addSector(String eventId, CreateEventSectorRequest request) {
        Event event = this.getEventValidated(eventId);

        VenueResponse venue = venueService.findVenueById(event.getVenueId());

        Map<String, SectorResponse> venueSectors = venue.sectors().stream()
                .collect(Collectors.toMap(SectorResponse::id, sector -> sector));

        SectorResponse venueSector = venueSectors.get(request.sectorId());

        if (venueSector == null) throw new ResourceNotFoundException("Given sector does not belong to this event's venue.");

        EventSector eventSector = new EventSector(venueSector, request.price());
        event.addSector(eventSector); // Might throw IllegalEventStateException (409)

        eventRepository.save(event);

        return eventSector.getSectorId();
    }

    public void publishEvent(String eventId) {
        Event event = this.getEventValidated(eventId);

        event.publish();

        InitCapacityRequest ticketingRequest = InitCapacityRequest.fromDomain(event);

        if (!ticketingRequest.capacities().isEmpty()) {
            ticketingClient.initializeSectors(ticketingRequest);
        }

        eventRepository.save(event);
    }

    public Page<EventResponse> getAll(EventCriteriaQuery criteria, Pageable pageable) {
        Page<Event> eventsPage = eventCustomRepository.findEventsWithDynamicFilters(criteria, pageable);

        return eventsPage.map(EventResponse::fromDomain);
    }


    private Event getEventValidated(String eventId) {
        if (eventId == null) throw new InvalidInputException("Event ID cannot be null.", null);
        if (!ObjectId.isValid(eventId)) throw new InvalidInputException("Event ID format is not valid.", null);

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) throw new ResourceNotFoundException("No event found with given ID: " + eventId);

        return event;
    }
}
