package com.arquiweb.catalog.venues.service;

import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import com.arquiweb.catalog.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.catalog.venues.domain.Venue;
import com.arquiweb.catalog.venues.dto.response.VenueResponse;
import com.arquiweb.catalog.venues.dto.request.CreateVenueRequest;
import com.arquiweb.catalog.venues.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;


    public String createVenue(CreateVenueRequest request) {
        Venue venue = request.toDomain();

        return  venueRepository.save(venue).getId();
    }

    public VenueResponse findVenueById(String venueId) {

        if (!ObjectId.isValid(venueId))
            throw new InvalidInputException("Given venue ID format is invalid", null);

        Optional<Venue> optionalVenue = venueRepository.findById(venueId);

        if (optionalVenue.isEmpty())
            throw new ResourceNotFoundException("No venue found with the given ID: " + venueId);

        Venue venue = optionalVenue.get();

        return VenueResponse.fromDomain(venue);
    }

}
