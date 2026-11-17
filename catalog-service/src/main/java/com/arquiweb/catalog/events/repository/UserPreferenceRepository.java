package com.arquiweb.catalog.events.repository;

import com.arquiweb.catalog.events.domain.UserPreference;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPreferenceRepository extends MongoRepository<UserPreference, UUID> {

    Optional<UserPreference> getById(UUID id);
}
