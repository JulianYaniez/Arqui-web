package com.arquiweb.ticketing.capacity.service;

import com.arquiweb.ticketing.capacity.domain.SectorCapacity;
import com.arquiweb.ticketing.capacity.repository.SectorCapacityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectorCapacityService {

    private final SectorCapacityRepository sectorCapacityRepository;

}
