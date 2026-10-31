package com.arquiweb.catalog.events.repository;

import com.arquiweb.catalog.events.domain.Event;
import com.arquiweb.catalog.events.domain.enums.EventStatus;
import com.arquiweb.catalog.events.dto.request.EventCriteriaQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
public class EventCustomRepository {

    private final MongoTemplate mongoTemplate;

    public Page<Event> findEventsWithDynamicFilters(EventCriteriaQuery criteria, Pageable pageable) {

        Query query = new Query();

        // Redo later with JWT role
        if (criteria == null || !criteria.admin()) {
            query.addCriteria(Criteria.where("status").is(EventStatus.PUBLISHED));
        }

        if (criteria != null) {

            if (criteria.name() != null && !criteria.name().isBlank()) {
                query.addCriteria(Criteria.where("name").regex(criteria.name()));
            }

            if (criteria.type() != null) {
                query.addCriteria(Criteria.where("type").is(criteria.type()));
            }

            if (criteria.participant() != null) {
                Pattern participantRegex = Pattern.compile(Pattern.quote(criteria.participant()), Pattern.CASE_INSENSITIVE);
                query.addCriteria(
                        Criteria.where("participants").elemMatch(new Criteria().regex(participantRegex))
                );
            }

            if (criteria.city() != null) {
                query.addCriteria(Criteria.where("city").regex(criteria.city()));
            }

            if (criteria.afterDate() != null) {
                query.addCriteria(Criteria.where("dateTime").gte(criteria.afterDate()));
            }

            if (criteria.beforeDate() != null) {
                query.addCriteria(Criteria.where("dateTime").lte(criteria.beforeDate()));
            }

            query.collation(Collation.of("en").strength(1));
        }

        long totalCount = mongoTemplate.count(query, Event.class);

        query.with(pageable);
        List<Event> events = mongoTemplate.find(query, Event.class);

        return new PageImpl<>(events, pageable, totalCount);
    }

}
