package com.arquiweb.catalog.venues.repository;

import com.arquiweb.catalog.venues.domain.Venue;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VenueRepository extends MongoRepository<Venue, String> {
}
