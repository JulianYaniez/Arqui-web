package org.arqui.tpe_3.services;


import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.commands.SaveDegreeDTO;
import org.arqui.tpe_3.dtos.queries.DegreeDTO;
import org.arqui.tpe_3.dtos.queries.DegreeEnrollmentsDTO;
import org.arqui.tpe_3.dtos.queries.DegreeReportDTO;
import org.arqui.tpe_3.exceptions.ResourceNotFoundException;
import org.arqui.tpe_3.repositories.interfaces.DegreeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DegreeService {
    
    private final DegreeRepository degreeRepository;

    @Transactional
    public UUID save(SaveDegreeDTO degree) {
        return degreeRepository.save(degree.toEntity());
    }

    @Transactional(readOnly = true)
    public DegreeDTO getById(UUID id) {
        var degree = degreeRepository.findById(id).orElse(null);
        if (degree == null) {
            throw new ResourceNotFoundException("Degree with id " + id + " not found");
        }
        return DegreeDTO.from(degree);
    }


    // get all students by career (f)
    @Transactional(readOnly = true)
    public List<DegreeEnrollmentsDTO> getEnrollments(){
        return  degreeRepository.getEnrollments();
    }

    // get report careers (h)
    @Transactional(readOnly = true)
    public List<DegreeReportDTO> getReport(){
        return degreeRepository.getReports();
    }


    @Transactional(readOnly = true)
    public List<DegreeDTO> getDegrees(){
        return degreeRepository.findAll().stream()
                .map(DegreeDTO::from)
                .toList();
    }
}
