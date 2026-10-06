package org.arqui.tpe_3.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.queries.StudentDTO;
import org.arqui.tpe_3.services.DegreeService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/degrees")
@RequiredArgsConstructor
public class DegreeController {

    private final DegreeService degreeService;

    // Get all students by career (f)
    @GetMapping()
    public List<StudentDTO> getStudentsByCareer(
            @RequestParam String careerName
    ) {
        return degreeService.getByCareer(careerName);
    }

    // Get report careers (h)
    @GetMapping("/reports")
    public List<StudentDTO> getReportCareers() {
        return degreeService.getReportCareers();
    }
}