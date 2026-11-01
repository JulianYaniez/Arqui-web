package com.arquiweb.ticketing.reservation.service;

import com.arquiweb.ticketing.inventory.dto.internal.CapacityRefundDto;
import com.arquiweb.ticketing.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationCleanupJob {

    private final ReservationService reservationService;
    private final InventoryService inventoryService;

    @Scheduled(fixedRate = 60000) // 60s/1m
    @Transactional
    public void cleanup() {
        LocalDateTime now = LocalDateTime.now();

        List<UUID> toExpire = reservationService.findExpired(now);

        reservationService.cancelReservations(toExpire);
    }

}
