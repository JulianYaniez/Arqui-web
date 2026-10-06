package org.arqui.tpe_3.services;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.queries.StudentDTO;
import org.arqui.tpe_3.repositories.impl.StudentRepositoryImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {
    
    private StudentRepositoryImpl studentRepository;

    // Register student (a)
    public void registerStudent(){ }

    // Search all students (c)
    public List<StudentDTO> getStudents() {
        return null;
    }

    // Search student by ID (d)
    public StudentDTO findStudentsById(UUID id) {
        return null;
    }

    // Search student by genre (e)
    public StudentDTO findStudentsByGenre(String genre) {
        return null;
    }

}
