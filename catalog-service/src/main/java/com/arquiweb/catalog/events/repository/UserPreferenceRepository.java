package com.arquiweb.catalog.events.repository;

import com.arquiweb.catalog.events.domain.UserPreference;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface UserPreferenceRepository extends MongoRepository<UserPreference, UUID> {

}
