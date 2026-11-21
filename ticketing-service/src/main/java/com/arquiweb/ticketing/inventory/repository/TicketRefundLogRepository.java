package com.arquiweb.ticketing.inventory.repository;

import com.arquiweb.ticketing.inventory.domain.TicketRefundLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketRefundLogRepository extends JpaRepository<TicketRefundLog, UUID> {
}