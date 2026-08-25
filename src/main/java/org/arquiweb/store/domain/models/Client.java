package org.arquiweb.store.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Client {
    private UUID id;
    private String name;
    private String email;

    public Client(String name, String email) {
        this.id = UUID.ofEpochMillis(System.currentTimeMillis()); // Generate UUID v7
        this.name = name;
        this.email = email;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        this.name = name;
    }
    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        this.email = email;
    }
}
