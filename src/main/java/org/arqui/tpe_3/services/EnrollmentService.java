package org.arqui.tpe_3.services;

import java.util.List;

import org.arqui.tpe_3.dtos.StudentDTO;
import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.repositories.implementation.JpaEnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service 
public class EnrollmentService {
    
    @Autowired 
    private JpaEnrollmentRepository repository;

    // Enroll a student in a degree (b)
    public void entrollStudent(Student student){ 
        // hacer cuando esten los JPA
    }

    // Search for students in a degree program (g)
    public List<StudentDTO> searchStudentsInProgram(String city) { 
        return null;
    }
}
