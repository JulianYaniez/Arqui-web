package org.arqui.tpe_3.controllers;

import org.arqui.tpe_3.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Register student (a)
    public void registerStudent(){ 
        studentService.registerStudent();
    }

    // Search all students (c)
    public void searchStudents() {
        studentService.searchStudents();
    }

    // Search student by ID (d)
    public void searchStudentById() { 
        studentService.searchStudentById();
    }

    // Search student by genre (e)
    public void searchStudentByGenre() { 
        studentService.searchStudentByGenre();
    }

}