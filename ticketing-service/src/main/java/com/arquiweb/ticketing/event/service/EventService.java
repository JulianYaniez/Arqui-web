package com.arquiweb.ticketing.event.service;

import com.arquiweb.ticketing.event.domain.TicketingEvent;
import com.arquiweb.ticketing.event.dto.requests.InitEventRequest;
import com.arquiweb.ticketing.event.repository.TicketingEventRepository;
import com.arquiweb.ticketing.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.ticketing.infrastructure.messaging.NotificationEventProducer;
import com.arquiweb.ticketing.inventory.service.InventoryService;
import com.arquiweb.ticketing.reservation.domain.Reservation;
import com.arquiweb.ticketing.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final InventoryService inventoryService;
    private final TicketingEventRepository ticketingEventRepository;
    private final ReservationService reservationService;
    private final NotificationEventProducer notificationEventProducer;

    public void initEvent(InitEventRequest request) {
        TicketingEvent event = new TicketingEvent(request.eventId(), true, request.isHighDemand());

        inventoryService.initSector(request.eventId(), request.capacities(), request.seats());

        ticketingEventRepository.save(event);
    }

    // public void validateEvent(String eventId) {
    //     TicketingEvent event = ticketingEventRepository.findById(eventId)
    //             .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

    //     if (!event.getSalesEnabled()) throw new InvalidEventException("Given event has already happened or was canceled.");
    // }

    public void cancelEvent(String eventId) {
        TicketingEvent event = ticketingEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        List<UUID> clientIds = reservationService.cancelByEvent(eventId).stream()
                .map(Reservation::getClientId)
                .toList();

        event.setSalesEnabled(false);
        notificationEventProducer.publishEventChanged(clientIds, eventId, "CANCELLED");
    }

}
