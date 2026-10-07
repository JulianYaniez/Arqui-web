package org.arqui.tpe_3.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.commands.SaveDegreeDTO;
import org.arqui.tpe_3.dtos.queries.DegreeEnrollmentsDTO;
import org.arqui.tpe_3.dtos.queries.DegreeReportDTO;
import org.arqui.tpe_3.services.DegreeService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/degrees")
@RequiredArgsConstructor
public class DegreeController {

    private final DegreeService degreeService;

    @PostMapping
    public void save(@RequestBody SaveDegreeDTO degree) {
        degreeService.save(degree);
    }

    @PostMapping
    public void saveAll(@RequestBody List<SaveDegreeDTO> degrees) {
        degreeService.saveAll(degrees);
    }

    // Get all students by career (f)
    @GetMapping()
    public List<DegreeEnrollmentsDTO> getStudentsByCareer() {
        return degreeService.getEnrollments();
    }

    // Get report careers (h)
    @GetMapping("/reports")
    public List<DegreeReportDTO> getReport() {
        return degreeService.getReport();
    }
}