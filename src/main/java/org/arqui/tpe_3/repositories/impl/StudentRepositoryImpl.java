package org.arqui.tpe_3.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.queries.StudentFilterDTO;
import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.enums.Genre;
import org.arqui.tpe_3.repositories.interfaces.StudentRepository;
import org.arqui.tpe_3.repositories.jpa.StudentJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentRepository {

    private final StudentJpaRepository studentRepository;

    @Override
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Optional<Student> findById(UUID id) {
        return studentRepository.findById(id);
    }

    @Override
    public List<Student> filterStudents(StudentFilterDTO filter, Pageable pageable) {
        var students = studentRepository.filterStudents(
                filter.genre(),
                filter.city(),
                filter.degreeId(),
                pageable
        );

        return students.toList();
    }

    @Override
    public List<Student> getStudents(String column, String order) {

        Sort.Direction direction = Sort.Direction.ASC;

        if(order.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Set<String> validColumns = Set.of("name", "city", "genre", "dni", "recordNumber");

        if(!validColumns.contains(column)) {
            column = "recordNumber";
        }

        Sort sort = Sort.by(direction, column);
        return studentRepository.findAll(sort);
    }

    @Override
    public List<Student> getByDegreeAndCity(UUID degreeId, String city) {
        return studentRepository.getByDegreeAndCity(degreeId, city);
    }

    @Override
    public Optional<Student> getByRecordNumber(String RecordBook) {
        return studentRepository.getByRecordNumber(RecordBook);
    }

    @Override
    public List<Student> getByGenre(Genre genre) {
        return studentRepository.getByGenre(genre);
    }

    @Override
    public boolean existsByRecordNumber(String recordNumber) {
        return studentRepository.existsByRecordNumber(recordNumber);
    }
}