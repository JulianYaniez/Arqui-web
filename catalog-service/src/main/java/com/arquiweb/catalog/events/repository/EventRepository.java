package com.arquiweb.catalog.events.repository;

import com.arquiweb.catalog.events.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;

public interface EventRepository extends MongoRepository<Event, String> {

    Page<Event> findByDateTimeGreaterThan(LocalDateTime dateTimeIsGreaterThan, Pageable pageable);
}
