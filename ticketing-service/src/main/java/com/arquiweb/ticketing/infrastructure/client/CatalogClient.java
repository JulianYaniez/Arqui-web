package com.arquiweb.ticketing.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "catalog-client", url = "${catalog.service.url}")
public interface CatalogClient {

}
