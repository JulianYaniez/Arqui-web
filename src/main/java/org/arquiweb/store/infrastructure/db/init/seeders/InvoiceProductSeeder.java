package org.arquiweb.store.infrastructure.db.init.seeders;

import org.arquiweb.store.application.ports.repositories.InvoiceProductRepository;
import org.arquiweb.store.domain.models.Invoice;
import org.arquiweb.store.domain.models.InvoiceProduct;
import org.arquiweb.store.infrastructure.db.init.readers.Reader;

import java.util.ArrayList;
import java.util.List;

public class InvoiceProductSeeder implements Seeder {
    
    // Repositorio que se utiliza para guardar los clientes en la base de datos
    private final InvoiceProductRepository repository; 
    // Reader encargado de leer los datos del archivo CSV
    private final Reader reader;

    public InvoiceProductSeeder(InvoiceProductRepository repository, Reader reader){
        this.repository = repository;
        this.reader = reader;
    }

    public void seed() {
        
        var rows = reader.read("invoice-products.csv");
        List<InvoiceProduct> invoiceProducts = new ArrayList<>();

        rows.forEach(row -> {
            invoiceProducts.add(
                    new invoiceProduct(
                            row.get("quantity")
                    )
            );
        });

         // Guarda todos los invoice de la lista en la base de datos
        repository.save(invoiceProducts);
    }
}

