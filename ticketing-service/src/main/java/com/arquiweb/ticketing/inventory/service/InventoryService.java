package com.arquiweb.ticketing.inventory.service;

import com.arquiweb.ticketing.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.ticketing.inventory.domain.SectorCapacity;
import com.arquiweb.ticketing.inventory.domain.Ticket;
import com.arquiweb.ticketing.inventory.domain.enums.TicketStatus;
import com.arquiweb.ticketing.inventory.domain.exceptions.IllegalTicketStateChangeException;
import com.arquiweb.ticketing.inventory.dto.internal.CapacityRefundDto;
import com.arquiweb.ticketing.inventory.dto.requests.InitInventoryRequest;
import com.arquiweb.ticketing.inventory.dto.responses.SectorAvailabilityResponse;
import com.arquiweb.ticketing.inventory.repository.SectorCapacityRepository;
import com.arquiweb.ticketing.inventory.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final SectorCapacityRepository sectorCapacityRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public void initSector(InitInventoryRequest request) {

        List<Ticket> seats = request.seats().stream()
                .map( sectorSeats -> {
                    return sectorSeats.toTickets(request.eventId());
                })
                .flatMap(List::stream)
                .toList();

        List<SectorCapacity> capacities = request.capacities().stream()
                .map(sector -> {
                    return sector.toCapacity(request.eventId());
                })
                .toList();

        if (!capacities.isEmpty()) {
            sectorCapacityRepository.saveAll(capacities);
        }

        if (!capacities.isEmpty()) {
            ticketRepository.saveAll(seats);
        }
    }

    public SectorAvailabilityResponse findSectorAvailability(String eventId, String sectorId) {
        SectorCapacity sectorCapacity = sectorCapacityRepository.findByEventIdAndSectorId(eventId, sectorId)
                .orElse(null);

        if (sectorCapacity != null) {
            return SectorAvailabilityResponse.fromCapacity(sectorCapacity);
        }


        List<Ticket> tickets = ticketRepository.findByEventIdAndSectorId(eventId, sectorId);

        if (!tickets.isEmpty()) {
            return SectorAvailabilityResponse.fromSeat(tickets);
        }

        throw new ResourceNotFoundException("Given sector or event does not exist or is not available.");
    }



    public SectorCapacity getSectorCapacity(String eventId, String sectorId) {
        SectorCapacity sector = sectorCapacityRepository.findByEventIdAndSectorId(eventId, sectorId)
                .orElseThrow(() -> new ResourceNotFoundException("No sector exists with given event and sector IDs."));


        return sector;
    }

    public void saveSectorCapacity(SectorCapacity sectorCapacity) {
        this.sectorCapacityRepository.save(sectorCapacity);
    }

    public List<Ticket> findTicketWithIdIn(List<UUID> ticketIds) {
        return ticketRepository.findByIdIn(ticketIds);
    }

    public void releaseNumberedTickets(List<UUID> reservations) {
        ticketRepository.unlockNumberedTickets(reservations, TicketStatus.AVAILABLE);
    }

    public void deleteUnnumberedTickets(List<UUID> reservations) {
        ticketRepository.deleteExpiredUnnumbered(reservations);
    }

    public List<CapacityRefundDto> countCapacities(List<UUID> reservations) {
        return ticketRepository.countUnnumberedCapacitiesToRefund(reservations);
    }

    public void refundCapacity(CapacityRefundDto refund) {
        sectorCapacityRepository.refundCapacity(refund.eventId(), refund.sectorId(), refund.count());
    }

    public void confirmTickets(Set<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() != TicketStatus.LOCKED_TEMPORARY) throw new IllegalTicketStateChangeException("Given ticket/s is not valid.");
            ticket.setStatus(TicketStatus.SOLD);
            ticket.setQrToken(UUID.randomUUID().toString());
        }
    }

    public void useTicket(String qrCode){
        Ticket ticket = ticketRepository.findByQrToken(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException("This QR token has no ticket assigned."));

        if (ticket.getStatus() != TicketStatus.SOLD) throw new  IllegalTicketStateChangeException("Given ticket status is not valid.");

        ticket.setStatus(TicketStatus.USED);
    }
}
