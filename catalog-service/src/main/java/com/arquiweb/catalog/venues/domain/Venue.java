package com.arquiweb.catalog.venues.domain;

import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "venues")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Venue {

    @Id
    private String id;

    private String name;
    private String address;
    private String city;
    private Location location;

    @Indexed
    private List<Sector> sectors;

    public Venue(
            String name,
            String address,
            String city,
            Location location,
            List<Sector> sectors
    ) {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.isBlank()) errors.put("name", "Venue name cannot be null or blank");

        if (address == null || address.isBlank()) errors.put("address", "Venue address cannot be null or blank");

        if (city == null || city.isBlank()) errors.put("city", "Venue city cannot be null or blank");

        if (location == null) errors.put("location", "Venue location cannot be null");

        if (sectors == null || sectors.isEmpty()) errors.put("sectors", "Venue must have at least one sector.");

        if (!errors.isEmpty()) throw new InvalidInputException("Given venue has invalid field/s.", errors);

        this.name = name;
        this.address = address;
        this.city = city;
        this.location = location;
        this.sectors = sectors;
    }
}

