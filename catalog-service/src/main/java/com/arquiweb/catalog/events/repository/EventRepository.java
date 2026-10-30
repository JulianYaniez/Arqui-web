package com.arquiweb.catalog.events.repository;

import com.arquiweb.catalog.events.domain.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, String> {
}
