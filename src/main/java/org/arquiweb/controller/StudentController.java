package org.arquiweb.controller;

import org.arquiweb.dto.queries.StudentDTO;
import org.arquiweb.services.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("students")
@Api(value = "StudentController", description = "REST API Students")

public class StudentController {

    @Qualifier("studentService")
    @Autowired
    private StudentService service;

    public StudentController (@Qualifier("studentService") StudentService service){
        this.service = service;
    }

    @ApiOperation(value = "Get list of students by enrollementId ", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success OK")
    })
    @getMapping("/{enrrollementId}")
    public void enrollStudent(UUID studentId, UUID degreeId){
        service.enrollStudent(studentId, degreeId);
    }

    // que kessy siga dsp
    public StudentDTO findByRecordNumber(String recordNumber) {
        return service.findByRecordNumber(recordNumber);
    }



}
