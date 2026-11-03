package com.arquiweb.management.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "catalog-client", url = "${app.feign.catalog.url}")
public interface CatalogClient {
}
