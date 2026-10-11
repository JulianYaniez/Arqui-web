package org.arqui.tpe_3.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.commands.SaveDegreeDTO;
import org.arqui.tpe_3.dtos.queries.DegreeEnrollmentsDTO;
import org.arqui.tpe_3.dtos.queries.DegreeReportDTO;
import org.arqui.tpe_3.services.DegreeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/degrees")
@RequiredArgsConstructor
@Tag(
        name = "Degrees",
        description = "Endpoints for degree management."
)
public class DegreeController {

    private final DegreeService degreeService;

    @Operation(
            summary = "Save a degree."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "Entity saved successfully.",
                headers = {
                    @Header(
                        name = "Location",
                        description = "Created entity's URI.",
                        schema = @Schema(type = "string")
                    )
                }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request data was faulty."
            )
        }
    )
    @PostMapping
    public ResponseEntity<Void> save(
            @Valid @RequestBody SaveDegreeDTO degree
    ) {
        UUID id = degreeService.save(degree);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // Get all students by career (f)
    @Operation(
            summary = "Get amount of enrolled students per degree. (2-f)"
    )
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "Data fetched successfully."
            )
    )
    @GetMapping("/enrollments")
    public ResponseEntity<List<DegreeEnrollmentsDTO>> getStudentsByCareer() {
        var enrollments = degreeService.getEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    // Get report careers (h)@Operation(
    @Operation(
            summary = "Get degree's enrollments and graduates per year. (2-h)"
    )
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "Data fetched successfully."
            )
    )
    @GetMapping("/reports")
    public ResponseEntity<List<DegreeReportDTO>> getReport() {
        var reports = degreeService.getReport();
        return ResponseEntity.ok(reports);
    }
}