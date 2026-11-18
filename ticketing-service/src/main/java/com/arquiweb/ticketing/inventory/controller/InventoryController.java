package com.arquiweb.ticketing.inventory.controller;

import com.arquiweb.ticketing.event.dto.requests.InitEventRequest;
import com.arquiweb.ticketing.inventory.dto.responses.SectorAvailabilityResponse;
import com.arquiweb.ticketing.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Management", description = "Operations pertaining inventory management.")
public class InventoryController {

    private  final InventoryService inventoryService;


    @Operation(summary = "Find sector availability. Returns both available and not.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sector data found."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Selected event sector does not exist."
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Selected event has already happened or was cancelled."
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
