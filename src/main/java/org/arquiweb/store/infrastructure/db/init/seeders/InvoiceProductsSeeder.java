package org.arquiweb.store.infrastructure.db.init.seeders;

import org.arquiweb.store.application.ports.repositories.InvoiceProductRepository;
import org.arquiweb.store.domain.models.InvoiceProduct;
import org.arquiweb.store.infrastructure.db.engines.Database;
import org.arquiweb.store.infrastructure.db.engines.DatabaseFactory;
import org.arquiweb.store.infrastructure.db.init.readers.Reader;
import org.arquiweb.store.infrastructure.db.utils.RelationalDatabaseUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InvoiceProductsSeeder implements Seeder {
    
    // Repositorio que se utiliza para guardar los clientes en la base de datos
    private final InvoiceProductRepository repository; 
    // Reader encargado de leer los datos del archivo CSV
    private final Reader reader;

    public InvoiceProductsSeeder(InvoiceProductRepository repository, Reader reader){
        this.repository = repository;
        this.reader = reader;
    }

    public void seed() {
        Database db = DatabaseFactory.getRelationalDatabase();
        if (RelationalDatabaseUtils.isTablePopulated(db, "invoice_products"))
            return;

        var rows = reader.read("invoice-products.csv");
        List<InvoiceProduct> invoiceProducts = new ArrayList<>();

        rows.forEach(row -> {
            Integer quantity = Integer.valueOf(
                    row.get("quantity")
            );
            UUID invoiceId = UUID.nameUUIDFromBytes(row.get("invoiceId").getBytes(StandardCharsets.UTF_8));
            UUID productId = UUID.nameUUIDFromBytes(row.get("productId").getBytes(StandardCharsets.UTF_8));

            invoiceProducts.add(
                    new InvoiceProduct(
                            invoiceId,
                            productId,
                            quantity
                    )
            );
        });

         // Guarda todos los invoice de la lista en la base de datos
        repository.saveAll(invoiceProducts);
    }
}

