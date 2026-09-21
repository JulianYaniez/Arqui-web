package org.arquiweb.services;

import org.arquiweb.dto.commands.SaveStudentDTO;
import org.arquiweb.dto.queries.StudentDTO;
import org.arquiweb.entities.Degree;
import org.arquiweb.entities.Enrollment;
import org.arquiweb.entities.Student;
import org.arquiweb.enums.Genre;
import org.arquiweb.repositories.interfaces.DegreeRepository;
import org.arquiweb.repositories.interfaces.EnrollmentRepository;
import org.arquiweb.repositories.interfaces.StudentRepository;
import org.arquiweb.util.RepositoryProvider;

import java.util.*;

public class StudentService {

    private static StudentService instance;

    public static StudentService getInstance() {
        if (instance == null) {
            instance = new StudentService(
                    RepositoryProvider.getStudentRepository(),
                    RepositoryProvider.getEnrollmentRepository(),
                    RepositoryProvider.getDegreeRepository()
            );
        }
        return instance;
    }

    private StudentRepository studentRepository;
    private EnrollmentRepository enrollmentRepository;
    private DegreeRepository degreeRepository;

    private StudentService() {}

    private StudentService(StudentRepository studentRepository, EnrollmentRepository enrollmentRepository, DegreeRepository degreeRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.degreeRepository = degreeRepository;
    }

    public UUID save(SaveStudentDTO command) {
        Student student = command.toEntity();

        return studentRepository.save(student);
    }

    public void saveAll(List<SaveStudentDTO> commands) {
        List<Student> students = commands.stream()
                .map(SaveStudentDTO::toEntity)
                .toList();

        studentRepository.saveAll(students);
    }

    public void enrollStudent(UUID studentId, UUID degreeId) {
        Student student = studentRepository.getById(studentId).orElse(null);
        Degree degree = degreeRepository.getById(degreeId).orElse(null);

        if (student == null)
            throw  new IllegalArgumentException("Student does not exist");

        if (degree == null)
            throw  new IllegalArgumentException("Degree does not exist");

        enrollmentRepository.save(new Enrollment(student, degree));
    }

    public StudentDTO findByRecordNumber(String recordNumber) {
        Optional<Student> opt = studentRepository.getByRecordNumber(recordNumber);

        if (opt.isPresent()) {
            return StudentDTO.from(opt.get());
        } else {
            throw new NoSuchElementException("Student with record number " + recordNumber + " not found");
        }
    }

    public List<StudentDTO> getAll(String column, String order) {
        return studentRepository.getAll(column, order)
                .stream().map(StudentDTO::from)
                .toList();
    }

    public List<StudentDTO> getByGenre(Genre genre) {
        return studentRepository.getByGenre(genre)
                .stream().map(StudentDTO::from)
                .toList();
    }

    public List<StudentDTO> getByDegreeAndCity(UUID degreeId, String city) {
        return studentRepository.getByDegreeAndCity(degreeId, city)
                .stream().map(StudentDTO::from)
                .toList();
    }
}
