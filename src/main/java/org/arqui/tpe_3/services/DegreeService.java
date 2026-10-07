package org.arqui.tpe_3.services;


import java.util.List;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.commands.SaveDegreeDTO;
import org.arqui.tpe_3.dtos.queries.DegreeEnrollmentsDTO;
import org.arqui.tpe_3.dtos.queries.DegreeReportDTO;
import org.arqui.tpe_3.repositories.interfaces.DegreeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DegreeService {
    
    private DegreeRepository degreeRepository;

    @Transactional(readOnly = true)
    public void save(SaveDegreeDTO degree) {
        degreeRepository.save(degree.toEntity());
    }

    @Transactional(readOnly = true)
    public void saveAll(List<SaveDegreeDTO> degrees) {
        degreeRepository.saveAll(degrees.stream().map(SaveDegreeDTO::toEntity).toList());
    }

    // get all students by career (f)
    public List<DegreeEnrollmentsDTO> getEnrollments(){
        return  degreeRepository.getEnrollments();
    }

    // get report careers (h)
    public List<DegreeReportDTO> getReport(){
        return degreeRepository.getReports();
    }

}
