package com.arquiweb.ticketing.inventory.service;

import com.arquiweb.ticketing.inventory.domain.SectorCapacity;
import com.arquiweb.ticketing.inventory.domain.Ticket;
import com.arquiweb.ticketing.inventory.dto.requests.InitInventoryRequest;
import com.arquiweb.ticketing.inventory.dto.requests.SectorCapacityDto;
import com.arquiweb.ticketing.inventory.dto.requests.SectorSeatsDto;
import com.arquiweb.ticketing.inventory.repository.SectorCapacityRepository;
import com.arquiweb.ticketing.inventory.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final SectorCapacityRepository sectorCapacityRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public void initSector(InitInventoryRequest request) {

        List<Ticket> seats = request.seats().stream()
                .map(sector -> {
                    return sector.toTicket(request.eventId());
                })
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


}
