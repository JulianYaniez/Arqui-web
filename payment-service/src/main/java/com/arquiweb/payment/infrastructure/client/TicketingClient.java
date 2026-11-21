package com.arquiweb.payment.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


import java.util.UUID;

@FeignClient(name = "ticketing-client", url = "${app.feign.ticketing.url}")
public interface TicketingClient {

    @PostMapping("/reservation/{reservationId}/cancel")
    void releaseReservations(
            @PathVariable("reservationId") UUID reservationId,
            @RequestHeader("X-User-Role") String serviceRole
    );
}
