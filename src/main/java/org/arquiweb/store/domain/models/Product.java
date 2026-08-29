package org.arquiweb.store.domain.models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Product {
    private UUID id;
    private String name;
    private Integer value;

    public Product(String name, Integer value){
        setName(name);
        setValue(value);
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        this.name = name;
    }

    public void setValue(Integer value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        this.value = value;
    }

}