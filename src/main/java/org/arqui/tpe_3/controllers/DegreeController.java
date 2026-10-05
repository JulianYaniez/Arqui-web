package org.arqui.tpe_3.controllers;

import org.arqui.tpe_3.services.DegreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping ("/degree")
public class DegreeController {
    
    @Autowired 
    private DegreeService degreeService;

    // get all students by career (f)
    public void getByCareer(){
        degreeService.getByCareer();
    }

    // get report careers (h)
    public void getReportCareers(){ 
        degreeService.getReportCareers();
    }

}
