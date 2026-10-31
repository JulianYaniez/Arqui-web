package com.arquiweb.ticketing.capacity.controller;

import com.arquiweb.ticketing.capacity.dto.requests.CreateCapacityRequest;
import com.arquiweb.ticketing.capacity.service.SectorCapacityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/capacities")
@RequiredArgsConstructor
public class SectorCapacityController {

    private  final SectorCapacityService capacityService;


    @Operation(summary = "Initialize sector capacities. Used by Catalog Module")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Sectors initialized successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid fields in incoming request body."
            )
    })
    @PostMapping("/init")
    public ResponseEntity<Void> initSectorCapacity(
            @RequestBody CreateCapacityRequest request
    ) {
        capacityService.createSectorCapacityService(request);

        return ResponseEntity.noContent().build();
    }
}
