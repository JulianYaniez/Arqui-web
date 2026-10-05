package org.arqui.tpe_3.services;

import org.arqui.tpe_3.repositories.implementation.JpaEnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service 
public class EnrollmentService {
    
    @Autowired 
    private JpaEnrollmentRepository repository;

    // Enroll a student in a degree (b)
    public void entrollStudent(){ }

    // Search for students in a degree program (g)
    public void searchStudentsInProgram() { }

}
