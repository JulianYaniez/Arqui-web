package org.arqui.tpe_3.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.commands.SaveStudentDTO;
import org.arqui.tpe_3.dtos.queries.StudentDTO;
import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.repositories.impl.StudentRepositoryImpl;
import org.arqui.tpe_3.repositories.interfaces.StudentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {
    
    private StudentRepository studentRepository;

    // Register student (a)
    public void save(SaveStudentDTO student){
        studentRepository.save(student.toEntity());
    }

    // Search all students (c)
    public List<StudentDTO> getStudents() {
        return null;
    }

    // Search student by ID (d)
    public StudentDTO findStudentById(UUID id) {
        return studentRepository.findById(id)
                .map(StudentDTO::from)
                .orElseThrow(() -> new RuntimeException("Not found Student with id " + id));
    }

    // Search student by genre (e)
    public StudentDTO findStudentsByGenre(String genre) {
        return null;
    }

}
