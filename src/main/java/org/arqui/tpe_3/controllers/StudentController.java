package org.arqui.tpe_3.controllers;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.commands.SaveStudentDTO;
import org.arqui.tpe_3.dtos.queries.StudentDTO;
import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.enums.Genre;
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

    @PostMapping
    public void saveAll(@RequestBody List<SaveStudentDTO> request) {
        studentService.saveAll(request);
    }

    // Search all students (c)
    @GetMapping
    public List<StudentDTO> getStudents(
            @RequestParam String column,
            @RequestParam String order) {
        return studentService.getStudents(column, order);
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
    public List<StudentDTO> findStudentByGenre(@RequestParam Genre genre) {
        return studentService.findStudentsByGenre(genre);
    }

    @GetMapping
    public List<StudentDTO> getByDegreeAndCity(
            @RequestParam UUID degreeId,
            @RequestParam String city) {
        return studentService.getByDegreeAndCity(degreeId, city);
    }

    @GetMapping
    public StudentDTO getByRecordBook(@RequestParam String recordBook) {
        return studentService.getByRecordBook(recordBook);
    }

}