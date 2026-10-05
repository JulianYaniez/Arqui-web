package org.arqui.tpe_3.controllers;

import java.util.List;

import org.arqui.tpe_3.dtos.StudentDTO;
import org.arqui.tpe_3.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Register student (a)
    @RequestMapping(
        method = RequestMethod.POST, 
        produces = "application/json", 
        consumes = "application/json"
    )
    public void registerStudent(){ 
        studentService.registerStudent();
    }

    // Search all students (c)
    @RequestMapping(
        method = RequestMethod.GET, 
        produces = "application/json"
    )
    public List<StudentDTO> searchStudents() {
        return studentService.searchStudents();
    }

    // Search student by ID (d)
    @RequestMapping(
        method = RequestMethod.GET, 
        params = "id", produces = "application/json"
    )
    public StudentDTO searchStudentById(@RequestParam int id) { 
        return studentService.searchStudentById(id);
    }

    // Search student by genre (e)
    @RequestMapping(
        method = RequestMethod.GET, 
        params = "genre", 
        produces = "application/json"
    )
    public StudentDTO searchStudentByGenre(@RequestParam String genre) { 
        return studentService.searchStudentByGenre(genre);
    }

}