package org.arqui.tpe_3.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.commands.SaveStudentDTO;
import org.arqui.tpe_3.dtos.queries.StudentDTO;
import org.arqui.tpe_3.enums.Genre;
import org.arqui.tpe_3.repositories.interfaces.StudentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

    private StudentRepository studentRepository;

    // save student (a)
    @Transactional
    public void save(SaveStudentDTO student){
        studentRepository.save(student.toEntity());
    }

    @Transactional
    public void saveAll(List<SaveStudentDTO> students){
        studentRepository.saveAll(students.stream().map(SaveStudentDTO::toEntity).toList());
    }

    // Find all students (c)
    public List<StudentDTO> getStudents(String column, String order) {
        return studentRepository.getStudents(column, order).stream().map(StudentDTO::from).toList();
    }

    // Find student by ID (d)
    public StudentDTO findStudentById(UUID id) {
        return studentRepository.findById(id)
                .map(StudentDTO::from)
                .orElseThrow(() -> new RuntimeException("Not found Student with id " + id));
    }

    // Find students by genre (e)
    public List<StudentDTO> findStudentsByGenre(Genre genre) {
        return studentRepository.getByGenre(genre).stream().map(StudentDTO::from).toList();
    }

    public List<StudentDTO> getByDegreeAndCity(UUID degreeId, String city) {
        return studentRepository.getByDegreeAndCity(degreeId, city).stream().map(StudentDTO::from).toList();
    }

    public StudentDTO getByRecordBook(String recordBook) {
        return studentRepository.getByRecordBook(recordBook)
                .map(StudentDTO::from)
                .orElseThrow(() -> new RuntimeException("Not found Student with recordBook " + recordBook));
    }
}
