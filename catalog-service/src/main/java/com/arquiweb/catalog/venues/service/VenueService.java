package com.arquiweb.catalog.venues.service;

import com.arquiweb.catalog.venues.domain.Venue;
import com.arquiweb.catalog.venues.dto.request.CreateVenueRequest;
import com.arquiweb.catalog.venues.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;


    public String createVenue(CreateVenueRequest request) {
        Venue venue = request.toDomain();

        return  venueRepository.save(venue).getId();
    }

}
