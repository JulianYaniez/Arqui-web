package com.arquiweb.ticketing.event.repository;

import com.arquiweb.ticketing.event.domain.TicketingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketingEventRepository extends JpaRepository<TicketingEvent, String> {
}