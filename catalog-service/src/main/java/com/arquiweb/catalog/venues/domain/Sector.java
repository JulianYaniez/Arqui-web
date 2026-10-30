package com.arquiweb.catalog.venues.domain;

import com.arquiweb.catalog.infrastructure.exception.InvalidInputException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sector {

    String id;
    String name;
    boolean isNumbered;
    Integer capacity;
    Integer rows;
    Integer seatsPerRow;


    public Sector(
            String name,
            boolean isNumbered,
            Integer capacity,
            Integer rows,
            Integer seatsPerRow
    ) {
        Map<String, String > errors = new HashMap<>();

        if (name == null) errors.put("name", "Sector name cannot be null.");

        if (isNumbered) {

            if (capacity != null && capacity != 0) errors.put("capacity", "Numbered sectors cannot have a maximum capacity assigned.");

            if (rows == null) errors.put("rows", "Numbered sectors must have a number of rows assigned.");
            else if (rows <= 0) errors.put("rows", "Numbered sectors must have a positive positive number of rows.");

            if (seatsPerRow == null) errors.put("seatsPerRow", "Numbered sectors must have a number of seats per row.");
            else if (seatsPerRow <= 0) errors.put("seatsPerRow", "Numbered sectors must have a positive number of seats per row.");

        } else {

            if (capacity == null) errors.put("capacity", "Capacity-based sector capacity cannot be null.");
            else if (capacity <= 0) errors.put("capacity", "Capacity-based sector capacity must be positive.");

            if (rows != null) errors.put("rows", "Capacity-based sectors cannot have rows assigned.");
            if (seatsPerRow != null) errors.put("seatsPerRow", "Capacity-based sectors cannot have seats per row assigned.");
        }

        if (!errors.isEmpty()) throw new InvalidInputException("Given sector has invalid field/s.", errors);

        this.id = ObjectId.get().toString();
        this.name = name;
        this.isNumbered = isNumbered;
        this.capacity = capacity;
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
    }

}
