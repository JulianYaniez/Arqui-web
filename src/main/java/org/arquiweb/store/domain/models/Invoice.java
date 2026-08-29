package org.arquiweb.store.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class Invoice {
    private UUID id;
    private UUID clientId;

    public Invoice(UUID clientId){
        this.clientId = clientId;
    }

    public void setClientId(UUID clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        this.clientId = clientId;
    }

}
