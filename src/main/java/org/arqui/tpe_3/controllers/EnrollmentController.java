package org.arqui.tpe_3.controllers;

import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.services.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping ("/enrollments")
public class EnrollmentController {
    
    @Autowired 
    private EnrollmentService enrollmentService;
    
    // Enroll a student in a degree (b)
    @RequestMapping(
        method = RequestMethod.POST,
        params = "student",
        produces = "application/json",
        consumes = "application/json"
    )
    public void entrollStudent(@RequestParam Student student){ 
        enrollmentService.entrollStudent();
    }

    // Search for students in a degree program (filter by city) (g)
    @RequestMapping(
        method = RequestMethod.GET,
        params = "city",
        produces = "application/json"
    )
    public void searchStudentsInProgram(@RequestParam String city) { 
        enrollmentService.searchStudentsInProgram();
    }

}
