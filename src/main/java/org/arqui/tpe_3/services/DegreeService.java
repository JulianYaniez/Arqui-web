package org.arqui.tpe_3.services;


import org.arqui.tpe_3.repositories.implementation.JpaDegreeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service 
public class DegreeService {
    
    @Autowired 
    private JpaDegreeRepository repository;

    // get all students by career (f)
    public void getByCareer(){ }

    // get report careers (h)
    public void getReportCareers(){ }

}
