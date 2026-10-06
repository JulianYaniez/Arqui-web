package org.arqui.tpe_3.services;


import java.util.List;
import org.arqui.tpe_3.dtos.StudentDTO;
import org.arqui.tpe_3.repositories.implementation.DegreeRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service 
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
