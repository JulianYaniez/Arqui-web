package org.arqui.tpe_3.services;

import java.util.List;

import org.arqui.tpe_3.dtos.StudentDTO;
import org.arqui.tpe_3.repositories.implementation.JpaStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service 
public class StudentService {
    
    @Autowired 
    private JpaStudentRepository repository;

    // Register student (a)
    public void registerStudent(){ }

    // Search all students (c)
    public List<StudentDTO> searchStudents() {
        return null;
    }

    // Search student by ID (d)
    public StudentDTO searchStudentById(int id) {
        return null;
     }

    // Search student by genre (e)
    public StudentDTO searchStudentByGenre(String genre) {
        return null;
     }

}
