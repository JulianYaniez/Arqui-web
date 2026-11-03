package com.arquiweb.management.infrastructure.client;

import com.arquiweb.management.infrastructure.client.dto.requests.ReservationStateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "ticketing-client", url = "${app.feign.ticketing.url}")
public interface TicketingClient {

    @GetMapping("/reservation/{reservationId}/state")
    ReservationStateResponse getReservationById(@PathVariable("reservationId")UUID reservationId);

}
