package org.arqui.tpe_3.controllers;

import java.util.List;

import org.arqui.tpe_3.dtos.StudentDTO;
import org.arqui.tpe_3.services.DegreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/degrees")
public class DegreeController {

    @Autowired
    private DegreeService degreeService;


    // get all students by career (f)
    @RequestMapping(
        method = RequestMethod.GET,
        params = "career",
        produces = "application/json"
    )
    public List<StudentDTO> getStudentsByCareer(@RequestParam String career) {
        return degreeService.getByCareer(career);
    }

    // get report careers (h)
    @RequestMapping(
        method = RequestMethod.GET,
        produces = "application/json"
    )
    public List<StudentDTO> getReportCareers() {
        return degreeService.getReportCareers();
    }
}