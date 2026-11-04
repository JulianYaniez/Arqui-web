package com.arquiweb.management.refunding.service;

import com.arquiweb.management.infrastructure.client.CatalogClient;
import com.arquiweb.management.infrastructure.client.PaymentClient;
import com.arquiweb.management.infrastructure.client.TicketingClient;
import com.arquiweb.management.infrastructure.client.dto.requests.EventStateResponse;
import com.arquiweb.management.infrastructure.client.dto.requests.ReservationStateResponse;
import com.arquiweb.management.infrastructure.client.dto.responses.PaymentRefundRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundingService {

    private final RefundRequestRepository refundRequestRepository;
    private final TicketingClient ticketingClient;
    private final CatalogClient catalogClient;
    private final PaymentClient paymentClient;

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

        if (!reservation.clientId().equals(clientId)) throw new UnauthorizedRefundRequestException("Reservation does not belong to current client.");

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

        ticketingClient.setRequestRefund(reservation.reservationId());

        return refundRequestRepository.save(refundRequest).getId();
    }

    @Transactional
    public void acceptRefund(UUID refundRequestId, UUID adminId) {
        RefundRequest refundRequest = validateRefundRequest(refundRequestId);

        ReservationStateResponse reservation;
        try {
            reservation = ticketingClient.getReservationById(refundRequest.getReservationId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Reservation's event does not exist.");
        } catch (Exception e) {
            throw new ServiceUnavailableException("Something went wrong with Catalog service.");
        }

        refundRequest.accept(adminId);

        PaymentRefundRequest paymentRefundRequest = new PaymentRefundRequest(
                reservation.reservationId(),
                reservation.paymentId(),
                reservation.totalPrice()
        );

        try {
            ticketingClient.acceptRefund(reservation.reservationId());
            paymentClient.refundPayment(paymentRefundRequest);
        } catch (Exception e) {
            throw new ServiceUnavailableException("Something went wrong with Payment or Ticketing services.");
        }

        refundRequestRepository.save(refundRequest);
    }


    @Transactional
    public void rejectRefund(UUID refundRequestId, UUID adminId) {
        RefundRequest refundRequest = validateRefundRequest(refundRequestId);

        ReservationStateResponse reservation;
        try {
            reservation = ticketingClient.getReservationById(refundRequest.getReservationId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Reservation's event does not exist.");
        } catch (Exception e) {
            throw new ServiceUnavailableException("Something went wrong with Catalog service.");
        }

        refundRequest.reject(adminId);

        try {
            ticketingClient.rejectRefund(reservation.reservationId());
        } catch (Exception e) {
            throw new ServiceUnavailableException("Something went wrong with Ticketing services.");
        }

        refundRequestRepository.save(refundRequest);
    }

    @Transactional
    public void cancelEvent(String eventId) {
        try {
            catalogClient.cancelEvent(eventId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Event does not exist.");
        } catch (Exception e) {
            throw new ServiceUnavailableException("Something went wrong with Catalog service.");
        }

        List<ReservationStateResponse> reservations;
        try {
            reservations = ticketingClient.getReservationsByEventId(eventId, "PAID");
        } catch (Exception e) {
            throw new ServiceUnavailableException("Something went wrong with Ticketing service.");
        }

        List<PaymentRefundRequest> payments = new ArrayList<>();
        List<UUID> reservationIds = new ArrayList<>();

        for (ReservationStateResponse reservation : reservations) {
            payments.add(PaymentRefundRequest.fromReservation(reservation));
            reservationIds.add(reservation.reservationId());
        }

        try { // Should go Async
            paymentClient.bulkRefund(payments);
        } catch (Exception e) {
            throw new ServiceUnavailableException("Something went wrong with Payment service.");
        }

        try {
            ticketingClient.bulkCancelReservations(reservationIds);
        } catch (Exception e) {
            throw new ServiceUnavailableException("Something went wrong with Ticketing service.");
        }
    }


    private RefundRequest validateRefundRequest(UUID refundRequestId) {
        return refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund request does not exist."));
    }
}
