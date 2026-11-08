package com.arquiweb.ticketing.reservation.service;

import com.arquiweb.ticketing.event.service.EventService;
import com.arquiweb.ticketing.infrastructure.exception.InvalidInputException;
import com.arquiweb.ticketing.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.ticketing.infrastructure.utils.QrHandler;
import com.arquiweb.ticketing.inventory.domain.SectorCapacity;
import com.arquiweb.ticketing.inventory.domain.Ticket;
import com.arquiweb.ticketing.inventory.domain.enums.TicketStatus;
import com.arquiweb.ticketing.inventory.dto.internal.CapacityRefundDto;
import com.arquiweb.ticketing.inventory.service.InventoryService;
import com.arquiweb.ticketing.reservation.domain.Reservation;
import com.arquiweb.ticketing.reservation.domain.enums.ReservationStatus;
import com.arquiweb.ticketing.reservation.domain.exceptions.IllegalReservationStateChangeException;
import com.arquiweb.ticketing.reservation.domain.exceptions.OccupiedSpotException;
import com.arquiweb.ticketing.reservation.domain.exceptions.QrGenerationException;
import com.arquiweb.ticketing.reservation.dto.requests.ReserveNumberedRequest;
import com.arquiweb.ticketing.reservation.dto.requests.ReserveUnnumberedRequest;
import com.arquiweb.ticketing.reservation.dto.responses.ReservationResponse;
import com.arquiweb.ticketing.reservation.dto.responses.ReservationStateResponse;
import com.arquiweb.ticketing.reservation.dto.responses.TicketQrResponse;
import com.arquiweb.ticketing.reservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ReservationService {

    private final EventService eventService;

    public ReservationService(
           @Value("${app.config.reservation.max-tickets}") int maxTickets,
           @Value("${app.config.reservation.max-ttl}") int ttl,
           @Value("${app.url}") String appUrl,
           ReservationRepository reservationRepository,
           InventoryService inventoryService,
           EventService eventService) {
        this.maxTickets = maxTickets;
        this.ttl = ttl;
        this.appUrl = appUrl;
        this.reservationRepository = reservationRepository;
        this.inventoryService = inventoryService;
        this.eventService = eventService;
    }

    private final int ttl;
    private final int maxTickets;
    private final String appUrl;
    private final ReservationRepository reservationRepository;
    private final InventoryService inventoryService;

    @Transactional
    @Retryable(maxRetries = 3, includes = OptimisticLockingFailureException.class)
    public ReservationResponse reserveUnnumbered(ReserveUnnumberedRequest request, UUID clientId) {
        eventService.validateEvent(request.eventId());

        this.checkMaxTickets(request.eventId(), clientId, request.ticketQuantity());

        SectorCapacity sector = inventoryService.getSectorCapacity(request.eventId(), request.sectorId());

        sector.reduceCapacity(request.ticketQuantity());

        List<Ticket> tickets = Stream.generate(() -> new Ticket(sector.getEventId(), sector.getSectorId(), sector.getPrice()))
                .limit(request.ticketQuantity())
                .toList();

        Reservation reservation = new Reservation(
                clientId,
                sector.getEventId(),
                sector.getPrice().multiply(BigDecimal.valueOf(request.ticketQuantity())),
                tickets,
                ttl
        );

        inventoryService.saveSectorCapacity(sector);

        reservationRepository.save(reservation); // Redundant, just for legibility

        return ReservationResponse.fromDomain(reservation);
    }


    public ReservationResponse reserveNumbered(ReserveNumberedRequest request, UUID clientId) {
        eventService.validateEvent(request.eventId());

        checkMaxTickets(request.eventId(), clientId, request.ticketIds().size());

        List<Ticket> tickets = inventoryService.findTicketWithIdIn(request.ticketIds());

        if (tickets.size() != request.ticketIds().size()) throw new InvalidInputException("Some of the given ticket IDs is wrong or invalid.", null);

        boolean allAvailable = tickets.stream()
                .allMatch(t -> t.getStatus() == TicketStatus.AVAILABLE && t.getEventId().equals(request.eventId()));

        if (!allAvailable) throw new OccupiedSpotException("Some of the selected seats are not available.");;

        tickets.forEach(Ticket::lockTemporary);
        BigDecimal total = tickets.get(0).getPrice().multiply(BigDecimal.valueOf(tickets.size()));

        Reservation reservation = new Reservation(clientId, request.eventId(), total, tickets, ttl);
        reservationRepository.save(reservation);

        return ReservationResponse.fromDomain(reservation);
    }


    public ReservationStateResponse getReservationStatus(UUID reservationId) {
        return reservationRepository.findById(reservationId).map(ReservationStateResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("No reservation found with given ID."));
    }

    public void cancelByEvent(String eventId) {
        List<UUID> reservationIds = reservationRepository.findByEventIdAndStatus(eventId, ReservationStatus.PAID).stream()
                .map(Reservation::getId)
                .toList();

        this.cancelReservations(reservationIds);
    }

    @Transactional
    public void handlePaymentFailure(UUID reservation) {
        cancelReservations(List.of(reservation));
    }

    @Transactional
    public void setPendingRefund(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("No reservation found with given ID."));

        reservation.requestRefund();
    }

    @Transactional
    public void acceptRefund(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("No reservation found with given ID."));

        reservation.refund();

        List<CapacityRefundDto> capacities = inventoryService.countCapacities(List.of(reservation.getId()));

        for (CapacityRefundDto capacity : capacities) inventoryService.refundCapacity(capacity);
    }

    @Transactional
    public void rejectRefund(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("No reservation found with given ID."));

        reservation.rejectRefund();
    }



    public void cancelReservations(List<UUID> toCancel) {
        if (toCancel.isEmpty()) return;

        inventoryService.releaseNumberedTickets(toCancel);

        List<CapacityRefundDto> refunds = inventoryService.countCapacities(toCancel);

        for (CapacityRefundDto refund : refunds) {
            inventoryService.refundCapacity(refund);
        }

        inventoryService.deleteUnnumberedTickets(toCancel);

        expire(toCancel);
    }

    public List<ReservationStateResponse> getSoldReservationsByEvent(String eventId, String statusString) {
        ReservationStatus status;
        try {
            status = ReservationStatus.valueOf(statusString);
        } catch (Exception e) {
            throw new InvalidInputException("Given status is not valid", null);
        }

        List<Reservation> reservations = reservationRepository.findByEventIdAndStatus(eventId, status);
        return reservations.stream().map(ReservationStateResponse::fromDomain).toList();
    }

    public void expire(List<UUID> expired) {
        reservationRepository.expire(expired, ReservationStatus.EXPIRED);
    }

    public List<UUID> findExpired(LocalDateTime now) {
        return reservationRepository.findExpired(now, ReservationStatus.PENDING);
    }

    public void confirmReservation(UUID reservationId, UUID paymentId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("No reservation found with given ID."));

        if (reservation.getStatus() != ReservationStatus.PENDING) throw new IllegalReservationStateChangeException("Given reservation is not valid.");

        inventoryService.confirmTickets(reservation.getTickets());
        reservation.setStatus(ReservationStatus.PAID);
        reservation.setPaymentId(paymentId);
    }

    public TicketQrResponse getTicketQr(UUID reservationId, UUID ticketId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("No reservation found with given ID."));

        if (reservation.getStatus() != ReservationStatus.PAID) throw new IllegalReservationStateChangeException("Given reservation is not valid.");

        Ticket ticket = reservation.getTickets().stream()
                .filter(t -> t.getId().equals(ticketId)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No ticket found with given ID for this reservation"));

        try {
            String qrCode = QrHandler.generateQrBase64(ticket.getQrToken());
            String usageLink = appUrl + "/reservation/use?token=" + ticket.getQrToken();
            return new TicketQrResponse(ticketId, usageLink);


        } catch (Exception e) {
            throw new QrGenerationException("Something went wrong generating QR code.");
        }
    }

    public void spendTicket(String qrToken) {
        inventoryService.useTicket(qrToken);
    }

    private void checkMaxTickets(String eventId, UUID clientId, int amount) {
        if (amount > this.maxTickets)
            throw new InvalidInputException("Only " + maxTickets + " can be bought at once.", null);

        int previousTickets = reservationRepository.countTicketsByClientIdAndEventId(clientId, eventId);
        if (previousTickets + amount > this.maxTickets)
            throw new InvalidInputException("Given has previously bough more tickets (" + previousTickets + "), and the max limit is " + maxTickets + ".", null);
    }

}
