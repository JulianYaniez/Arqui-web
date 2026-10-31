package com.arquiweb.catalog.events.dto.request;

import com.arquiweb.catalog.events.domain.enums.EventType;

import java.time.LocalDateTime;
import java.util.List;

public record EventCriteriaQuery(
        String name,
        EventType type,
        String participant,
        String city,
        LocalDateTime afterDate,
        LocalDateTime beforeDate,
        // Remove after implementing JWT
        Boolean admin
) {
}
