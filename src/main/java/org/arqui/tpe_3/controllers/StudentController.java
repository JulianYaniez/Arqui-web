package org.arqui.tpe_3.controllers;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.StudentDTO;
import org.arqui.tpe_3.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private StudentService studentService;

    // Register student (a)
    // ** Must receive SaveStudentDTO
    @PostMapping
    public void registerStudent(){ 
        studentService.registerStudent();
    }

    // Search student by ID (d)
    @GetMapping("/{id}")
    public StudentDTO searchStudentById(
            @PathVariable UUID id
    ) {
        return studentService.findStudentsById(id);
    }

    // Search all students (c)
    @GetMapping
    public List<StudentDTO> searchStudents() {
        return studentService.getStudents();
    }

    // Search student by genre (e)
    @GetMapping
    public StudentDTO searchStudentByGenre(@RequestParam String genre) { 
        return studentService.findStudentsByGenre(genre);
    }

}