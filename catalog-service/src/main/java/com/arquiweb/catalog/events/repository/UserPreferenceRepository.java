package com.arquiweb.catalog.events.repository;

import com.arquiweb.catalog.events.domain.UserPreference;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.*;

public interface UserPreferenceRepository extends MongoRepository<UserPreference, UUID> {

    Optional<UserPreference> getById(UUID id);

    List<UserPreference> findByParticipantsInAndCitiesContaining(List<String> participants, String city);
}
