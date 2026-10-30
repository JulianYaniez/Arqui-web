package com.arquiweb.catalog.infrastructure.client;

import com.arquiweb.catalog.infrastructure.client.dtos.command.InitCapacityRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ticketing-client", url = "${ticketing.service.url}")
public interface TicketingClient {

    @PostMapping("/capacities/init")
    void initializeSectors(@RequestBody InitCapacityRequest request);

}
