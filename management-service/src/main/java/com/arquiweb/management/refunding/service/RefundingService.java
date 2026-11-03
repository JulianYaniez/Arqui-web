package com.arquiweb.management.refunding.service;

import com.arquiweb.management.infrastructure.client.CatalogClient;
import com.arquiweb.management.infrastructure.client.TicketingClient;
import com.arquiweb.management.infrastructure.client.dto.requests.EventStateResponse;
import com.arquiweb.management.infrastructure.client.dto.requests.ReservationStateResponse;
import com.arquiweb.management.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.management.infrastructure.exception.ServiceUnavailableException;
import com.arquiweb.management.infrastructure.exception.UnprocessableEntityException;
import com.arquiweb.management.refunding.domain.RefundRequest;
import com.arquiweb.management.refunding.domain.exceptions.InvalidRefundStatusException;
import com.arquiweb.management.refunding.domain.exceptions.UnauthorizedRefundRequestException;
import com.arquiweb.management.refunding.dto.requests.CreateRefundRequest;
import com.arquiweb.management.refunding.repository.RefundRequestRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundingService {

    private final RefundRequestRepository refundRequestRepository;
    private final TicketingClient ticketingClient;
    private final CatalogClient catalogClient;

    @Transactional
    public UUID createRefundRequest(CreateRefundRequest request, UUID clientId) {
        ReservationStateResponse reservation;

        try {
            reservation = ticketingClient.getReservationById(request.reservationId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("No reservation found with given ID.");
        } catch (Exception e) {
            throw new ServiceUnavailableException("Something went wrong with Ticketing service. Try Again later.");
        }

        if (!request.clientId().equals(clientId)) throw new UnauthorizedRefundRequestException("Reservation does not belong to current client.");

        if (!reservation.status().equalsIgnoreCase("PAID")) throw new InvalidRefundStatusException("Cannot refund reservation in current state.");

        EventStateResponse event;
        try {
            event = catalogClient.getEventById(reservation.eventId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Reservation's event does not exist.");
        } catch (Exception e) {
            throw new ServiceUnavailableException("Something went wrong with Catalog service.");
        }

        LocalDateTime maxRefundTime = event.dateTime().minus(Duration.ofHours(72));
        if (LocalDateTime.now().isAfter(maxRefundTime)) throw new UnprocessableEntityException("Cannot refund reservations under 72hs from the event time.");

        RefundRequest refundRequest = new RefundRequest(reservation.reservationId(), clientId, request.reason());

        
    }

}
