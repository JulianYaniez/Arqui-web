package org.arqui.tpe_3.services;


import java.util.List;

import lombok.RequiredArgsConstructor;
import org.arqui.tpe_3.dtos.queries.StudentDTO;
import org.arqui.tpe_3.repositories.impl.DegreeRepositoryImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DegreeService {
    
    private DegreeRepositoryImpl degreeRepository;

    // get all students by career (f)
    public List<StudentDTO> getByCareer(String career){
        return null;
    }

    // get report careers (h)
    public List<StudentDTO> getReportCareers(){ 
        return null;
    }

}
