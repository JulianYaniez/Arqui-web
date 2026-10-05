package org.arqui.tpe_3.services;

import org.arqui.tpe_3.repositories.implementation.JpaStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class StudentService {
    
    @Autowired 
    private JpaStudentRepository repository;

    // Register student (a)
    public void registerStudent(){ }

    // Search all students (c)
    public void searchStudents() {}

    // Search student by ID (d)
    public void searchStudentById() { }

    // Search student by genre (e)
    public void searchStudentByGenre() { }

}
