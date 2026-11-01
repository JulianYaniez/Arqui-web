package com.arquiweb.ticketing.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationCleanupJob {

    private final ReservationService reservationService;

    @Scheduled(fixedRate = 60000) // 60s/1m
    public void cleanup() {
        reservationService.expireOldReservations();
    }
}
