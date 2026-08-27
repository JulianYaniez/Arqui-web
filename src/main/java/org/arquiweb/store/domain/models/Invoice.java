package org.arquiweb.store.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Invoice {
    private UUID InvoiceId;
    private UUID ProductId;

    public Invoice(UUID ProductId){
        this.ProductId = ProductId;
    }

    public void setProductId(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        ProductId = productId;
    }

}
