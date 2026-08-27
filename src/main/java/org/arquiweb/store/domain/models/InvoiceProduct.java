package org.arquiweb.store.domain.models;

import java.util.UUID;

import lombok.Getter;

@Getter
public class InvoiceProduct {
    private UUID InvoiceId;
    private UUID ProductId;
    private Integer quantity;

    public InvoiceProduct(Integer quantity){
        setQuantity(quantity);
        setInvoiceId(InvoiceId);
        setProductId(ProductId);
    }

    public void setInvoiceId(UUID invoiceId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        InvoiceId = invoiceId;
    }

    public void setProductId(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        ProductId = productId;
    }

    public void setQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        this.quantity = quantity;
    }

}
