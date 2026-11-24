package com.arquiweb.ticketing.dev.service;

import com.arquiweb.ticketing.inventory.domain.SectorCapacity;
import com.arquiweb.ticketing.inventory.domain.Ticket;
import com.arquiweb.ticketing.inventory.domain.enums.TicketStatus;
import com.arquiweb.ticketing.inventory.repository.SectorCapacityRepository;
import com.arquiweb.ticketing.inventory.repository.TicketRepository;
import com.arquiweb.ticketing.reservation.domain.Reservation;
import com.arquiweb.ticketing.reservation.domain.enums.ReservationStatus;
import com.arquiweb.ticketing.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevService {

    private final SectorCapacityRepository sectorCapacityRepository;
    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;

    public void seed() {
        Random random = new Random();

        List<SectorCapacity> capacities = sectorCapacityRepository.findAll();
        List<Ticket> allNumbered = ticketRepository.findByIsNumbered(true);

        Map<String, Map<String, List<Ticket>>> ticketsByEventAndSector = allNumbered.stream()
                .collect(
                        Collectors.groupingBy(Ticket::getEventId,
                                Collectors.groupingBy(Ticket::getSectorId))
                );

        List<String> eventIds = new ArrayList<>(ticketsByEventAndSector.keySet());

        List<Reservation> reservations = new ArrayList<>();

        for (int i = 0; i < 100; i++) {

            if (random.nextBoolean()) {
                String eventId = eventIds.get(random.nextInt(eventIds.size()));
                List<String> sectorIds = new ArrayList<>(ticketsByEventAndSector.get(eventId).keySet());
                List<Ticket> ticketsBySector = ticketsByEventAndSector.get(eventId).get(sectorIds.get(random.nextInt(sectorIds.size())));

                int ticketsAmount = random.nextInt(4) + 1;
                if (ticketsAmount > ticketsBySector.size()) continue;

                List<Ticket> reservedTickets = new ArrayList<>();
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (int j = 0; j < ticketsAmount; j++) {
                    Ticket ticket = ticketsBySector.remove(ticketsBySector.size() - 1);
                    totalAmount = totalAmount.add(ticket.getPrice());
                    reservedTickets.add(ticket);
                }


                Reservation reservation = new Reservation(
                        UUID.randomUUID(),
                        eventId,
                        totalAmount,
                        reservedTickets,
                        10
                );

                reservation.setPaymentId(UUID.randomUUID());
                reservation.setStatus(ReservationStatus.PAID);
                reservation.setExpiresAt(null);
                reservedTickets.forEach(t -> {
                    t.setStatus(TicketStatus.SOLD);
                    t.setQrToken(UUID.randomUUID().toString());
                });

                reservations.add(reservation);
            } else {
                SectorCapacity sector = capacities.get(random.nextInt(capacities.size()));

                int ticketsAmount = random.nextInt(4) + 1;
                if (sector.getAvailableCapacity() < ticketsAmount) continue;
                List<Ticket> reservedTickets = new ArrayList<>();
                for (int j = 0; j < ticketsAmount; j++) {
                    Ticket ticket = new Ticket(
                        sector.getEventId(),
                        sector.getSectorId(),
                        sector.getPrice()
                    );
                    reservedTickets.add(ticket);
                }
                sector.setAvailableCapacity(sector.getAvailableCapacity() - ticketsAmount);

                Reservation reservation = new Reservation(
                        UUID.randomUUID(),
                        sector.getEventId(),
                        sector.getPrice().multiply(BigDecimal.valueOf(ticketsAmount)),
                        reservedTickets,
                        10
                );


                reservation.setPaymentId(UUID.randomUUID());
                reservation.setStatus(ReservationStatus.PAID);
                reservation.setExpiresAt(null);
                reservedTickets.forEach(t -> {
                    t.setStatus(TicketStatus.SOLD);
                    t.setQrToken(UUID.randomUUID().toString());
                });

                reservations.add(reservation);
            }
        }

        reservationRepository.saveAll(reservations);
    }

}
