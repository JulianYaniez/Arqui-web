package org.arqui.tpe_3.services;

import java.util.List;
import java.util.UUID;

import org.arqui.tpe_3.entities.Degree;
import org.arqui.tpe_3.entities.Enrollment;
import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.repositories.interfaces.DegreeRepository;
import org.arqui.tpe_3.repositories.interfaces.EnrollmentRepository;
import org.springframework.transaction.annotation.Transactional;
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
    private EnrollmentRepository enrollmentRepository;
    private DegreeRepository degreeRepository;

    @Transactional
    public void enrollStudent(UUID id, UUID degreeId) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
        Degree degree = degreeRepository.findById(degreeId).orElseThrow(() -> new RuntimeException("Degree not found"));

        enrollmentRepository.save(new Enrollment(student, degree));
    }

    // save student (a)
    @Transactional(readOnly = true)
    public void save(SaveStudentDTO student){
        studentRepository.save(student.toEntity());
    }

    @Transactional(readOnly = true)
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
