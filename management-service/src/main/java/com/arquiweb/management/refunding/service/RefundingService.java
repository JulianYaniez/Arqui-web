package com.arquiweb.management.refunding.service;

import com.arquiweb.management.infrastructure.client.TicketingClient;
import com.arquiweb.management.infrastructure.client.dto.requests.ReservationStateResponse;
import com.arquiweb.management.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.management.refunding.domain.exceptions.UnauthorizedRefundRequestException;
import com.arquiweb.management.refunding.dto.requests.CreateRefundRequest;
import com.arquiweb.management.refunding.repository.RefundRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundingService {

    private final RefundRequestRepository refundRequestRepository;
    private final TicketingClient ticketingClient;

    @Transactional
    public UUID createRefundRequest(CreateRefundRequest request, UUID clientId) {
        ReservationStateResponse reservation = ticketingClient.getReservationById(request.reservationId());

        if (reservation == null) throw new ResourceNotFoundException("Reservation not found with given ID.");
        if (reservation.clientId() != clientId) throw new UnauthorizedRefundRequestException("Given client doesn't own this reservation.");


    }

}
