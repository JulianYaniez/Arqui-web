package org.arqui.tpe_3.controllers;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.commands.SaveStudentDTO;
import org.arqui.tpe_3.dtos.queries.StudentDTO;
import org.arqui.tpe_3.services.StudentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private StudentService studentService;

    // Register student (a)
    // ** Must receive SaveStudentDTO
    @PostMapping
    public void save(@RequestBody SaveStudentDTO request){
        studentService.save(request);
    }

    // Search all students (c)
    @GetMapping
    public List<StudentDTO> getStudents() {
        return studentService.getStudents();
    }

    // Search student by ID (d)
    @GetMapping("/{id}")
    public StudentDTO findStudentById(
            @PathVariable UUID id
    ) {
        return studentService.findStudentById(id);
    }

    // Search student by genre (e)
    @GetMapping(params = "genre")
    public StudentDTO findStudentByGenre(@RequestParam String genre) {
        return studentService.findStudentsByGenre(genre);
    }

}