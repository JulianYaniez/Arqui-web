package com.arquiweb.ticketing.inventory.repository;

import com.arquiweb.ticketing.inventory.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
}