package com.arquiweb.ticketing.inventory.controller;

import com.arquiweb.ticketing.inventory.dto.requests.InitInventoryRequest;
import com.arquiweb.ticketing.inventory.dto.responses.SectorAvailabilityResponse;
import com.arquiweb.ticketing.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private  final InventoryService inventoryService;


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
            @RequestBody InitInventoryRequest request
    ) {
        inventoryService.initSector(request);

        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Find sector availability. Returns both available and not.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sector data found."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Selected event sector does not exist."
            )
    })
    @GetMapping("/event/{eventId}/sector/{sectorId}")
    public ResponseEntity<SectorAvailabilityResponse> getSectorAvailability(
            @PathVariable String eventId,
            @PathVariable String sectorId
    ) {
        SectorAvailabilityResponse availabilityResponse = inventoryService.findSectorAvailability(eventId, sectorId);

        return ResponseEntity.ok(availabilityResponse);
    }
}
