package com.arquiweb.ticketing.capacity.controller;

import com.arquiweb.ticketing.capacity.service.SectorCapacityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/capacities")
@RequiredArgsConstructor
public class SectorCapacityController {

    private  final SectorCapacityService service;

}
