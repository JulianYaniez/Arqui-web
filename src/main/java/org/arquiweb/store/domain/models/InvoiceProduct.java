package org.arquiweb.store.domain.models;

import java.util.UUID;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class InvoiceProduct {
    private UUID invoiceId;
    private UUID productId;
    private Integer quantity;

    public InvoiceProduct(UUID invoiceId, UUID productId, Integer quantity){
        setQuantity(quantity);
        setInvoiceId(invoiceId);
        setProductId(productId);
    }

    public void setInvoiceId(UUID invoiceId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        this.invoiceId = invoiceId;
    }

    public void setProductId(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        this.productId = productId;
    }

    public void setQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        this.quantity = quantity;
    }

}
