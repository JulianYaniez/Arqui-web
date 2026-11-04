package com.arquiweb.management.infrastructure.client;

import com.arquiweb.management.infrastructure.client.dto.requests.EventStateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "catalog-client", url = "${app.feign.catalog.url}")
public interface CatalogClient {

    @GetMapping("/event/{eventId}/state")
    EventStateResponse getEventById(@PathVariable("eventId") String id);

    @PostMapping("/event/{eventId}/cancel")
    void cancelEvent(@PathVariable("eventId") String eventId);
}
