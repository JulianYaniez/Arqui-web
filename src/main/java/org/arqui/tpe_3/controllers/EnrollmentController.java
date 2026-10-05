package org.arqui.tpe_3.controllers;

import org.arqui.tpe_3.services.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping ("/enrollments")
public class EnrollmentController {
    
    @Autowired 
    private EnrollmentService enrollmentService;
    
    // Enroll a student in a degree (b)
    public void entrollStudent(){ 
        enrollmentService.entrollStudent();
    }

    // Search for students in a degree program (g)
    public void searchStudentsInProgram() { 
        enrollmentService.searchStudentsInProgram();
    }

}
