package org.arqui.tpe_3.services;

import java.util.List;

import org.arqui.tpe_3.repositories.implementation.JpaStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class StudentService {
    
    @Autowired 
    private JpaStudentRepository repository;

    // Register student (a)
    public void registerStudent(){ }

    // Search all students (c)
    public List<StudentDTO> searchStudents() {}

    // Search student by ID (d)
    public List<StudentDTO> searchStudentById(int id) { }

    // Search student by genre (e)
    public List<StudentDTO> searchStudentByGenre(String genre) { }

}
