package org.arqui.tpe_3.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.enums.Genre;
import org.arqui.tpe_3.repositories.interfaces.StudentRepository;
import org.arqui.tpe_3.repositories.jpa.StudentJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StudentRepositoryImpl implements StudentRepository {

    private final StudentJpaRepository studentRepository;

    @Override
    public List<Student> getAll(String column, String order) {

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
        return null;
    }

    @Override
    public Optional<Student> getByRecordBook(String RecordBook) {
        return Optional.empty();
    }

    @Override
    public List<Student> getByGenre(Genre genre) {
        return List.of();
    }


}