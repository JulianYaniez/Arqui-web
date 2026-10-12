package org.arqui.tpe_3.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.controllers.exceptions.ErrorResponse;
import org.arqui.tpe_3.dtos.commands.SaveStudentDTO;
import org.arqui.tpe_3.dtos.queries.EnrollmentDTO;
import org.arqui.tpe_3.dtos.queries.StudentDTO;
import org.arqui.tpe_3.dtos.queries.StudentFilterDTO;
import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.services.StudentService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Tag(
        name = "Students",
        description = "Endpoint for student and enrollments management."
)
public class StudentController {

    private final StudentService studentService;

    // Register student (a)
    @Operation(
            summary = "Save a student. (2-a)"
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
                    description = "Request data was faulty.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
        }
    )
    @PostMapping
    public ResponseEntity<Void> save(
            @Valid @RequestBody SaveStudentDTO request
    ) {
        Student student = studentService.save(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{recordNumber}")
                .buildAndExpand(student.getRecordNumber())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    // d
    @Operation(
            summary = "Get student by record number. (2-d)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Resource fetched successfully."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No entity found with given id.",
                            content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/{recordNumber}")
    public StudentDTO getByRecordNumber(
            @Valid @PathVariable(name = "recordNumber") String recordNumber
    ) {
        return studentService.getByRecordNumber(recordNumber);
    }

    // c, e, g; all in one
    @Operation(
            summary = "Get students by criteria (genre, city, enrolled degrees). (2-c, 2-e, 2-g)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Resources fetched successfully"
                    )
            }
    )
    @GetMapping()
    public ResponseEntity<List<StudentDTO>> getStudents(
        StudentFilterDTO filter,
        Pageable pageable
    ) {
        var students = studentService.filterStudents(filter, pageable);

        return ResponseEntity.ok(students);
    }





    @Operation(
            summary = "Enroll student. (2-b)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Enrollment completed successfully.",
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
                            description = "Request data was faulty.",
                            content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/{recordNumber}/enrollments")
    public ResponseEntity<Void> enrollStudent(
            @PathVariable String recordNumber,
            @RequestBody UUID degreeId
    ) {
        studentService.enrollStudent(recordNumber, degreeId);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{degreeId}")
                .buildAndExpand(degreeId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @Operation(
            summary = "Get enrollments of a given student by record number."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Data fetched successfully."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description =  "Given record number does not exist.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/{recordNumber}/enrollments")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollments(
            @PathVariable String recordNumber
    ) {
        var enrollments = studentService.getEnrollmentsByRecordNumber(recordNumber);

        return ResponseEntity.ok(enrollments);
    }

    @Operation(
            summary = "Get a student's inscription to a given degree."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Data fetched successfully."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Given record number or degree id does not exist.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Given student is not enrolled to this degree.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/{recordNumber}/enrollments/{degreeId}")
    public ResponseEntity<EnrollmentDTO> getEnrollment(
            @PathVariable String recordNumber,
            @PathVariable UUID degreeId
    ) {
        EnrollmentDTO enrollment = studentService.getEnrollmentByRecordNumberAndDegree(recordNumber, degreeId);
        return ResponseEntity.ok(enrollment);
    }





    /*
    // Search student by ID
    @GetMapping("/{id}")
    public StudentDTO findStudentById(@PathVariable UUID id) {
        return studentService.findStudentById(id);
    }

    // Search all students (c)
    @GetMapping(params = {"column, order"})
    public List<StudentDTO> getStudents(
            @RequestParam(name = "column", required = false) String column,
            @RequestParam(name = "order", required = false) String order) {
        return studentService.getStudents(column, order);
    }

    // Search student by genre (e)
    @GetMapping(params = "genre")
    public List<StudentDTO> findStudentByGenre(@RequestParam(name = "genre", required = false) Genre genre) {
        return studentService.findStudentsByGenre(genre);
    }

    @GetMapping(params = {"degreeId", "city"})
    public List<StudentDTO> getByDegreeAndCity(
            @RequestParam(name = "degreeId", required = false) UUID degreeId,
            @RequestParam(name = "city", required = false) String city
    ) {
        return studentService.getByDegreeAndCity(degreeId, city);
    }
    */
}