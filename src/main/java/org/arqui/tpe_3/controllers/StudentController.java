package org.arqui.tpe_3.controllers;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.arqui.tpe_3.dtos.commands.SaveStudentDTO;
import org.arqui.tpe_3.dtos.queries.StudentDTO;
import org.arqui.tpe_3.entities.Enrollment;
import org.arqui.tpe_3.enums.Genre;
import org.arqui.tpe_3.services.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private StudentService studentService;

    // Register student (a)
    // ** Must receive SaveStudentDTO
    @PostMapping
    public void save(@RequestBody SaveStudentDTO request) {
        studentService.save(request);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Enrollment> enrollStudent(
            @PathVariable UUID id,
            @RequestBody UUID degreeId
    ) {
        return ResponseEntity.ok(studentService.enrollStudent(id, degreeId));
    }

    // Search all students (c)
    @GetMapping
    public List<StudentDTO> getStudents(
            @RequestParam(name = "column", required = false) String column,
            @RequestParam(name = "order", required = false) String order) {
        return studentService.getStudents(column, order);
    }

    // Search student by ID (d)
    @GetMapping("/{id}")
    public StudentDTO findStudentById(@PathVariable UUID id) {
        return studentService.findStudentById(id);
    }

    // Search student by genre (e)
    @GetMapping(params = "genre")
    public List<StudentDTO> findStudentByGenre(@RequestParam(name = "genre", required = false) Genre genre) {
        return studentService.findStudentsByGenre(genre);
    }

    @GetMapping
    public List<StudentDTO> getByDegreeAndCity(
            @RequestParam(name = "degreeId", required = false) UUID degreeId,
            @RequestParam(name = "city", required = false) String city) {
        return studentService.getByDegreeAndCity(degreeId, city);
    }

    @GetMapping
    public StudentDTO getByRecordBook(@RequestParam(name = "recordBook", required = false) String recordBook) {
        return studentService.getByRecordBook(recordBook);
    }
}