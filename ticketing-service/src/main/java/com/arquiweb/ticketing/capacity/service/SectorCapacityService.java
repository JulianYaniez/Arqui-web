package com.arquiweb.ticketing.capacity.service;

import com.arquiweb.ticketing.capacity.domain.SectorCapacity;
import com.arquiweb.ticketing.capacity.dto.requests.CreateCapacityRequest;
import com.arquiweb.ticketing.capacity.dto.requests.CreateSectorCapacityRequest;
import com.arquiweb.ticketing.capacity.repository.SectorCapacityRepository;
import com.arquiweb.ticketing.infrastructure.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorCapacityService {

    private final SectorCapacityRepository sectorCapacityRepository;

    public void createSectorCapacityService(CreateCapacityRequest request) {

        List<SectorCapacity> capacities = new ArrayList<>();
        for (CreateSectorCapacityRequest sector : request.capacities()) {
            capacities.add(new SectorCapacity(
                    request.eventId(),
                    sector.eventId(),
                    sector.totalCapacity()
            ));
        }

        if (!capacities.isEmpty()) {
            sectorCapacityRepository.saveAll(capacities);
        }
    }
}
