package org.arquiweb.store.infrastructure.db.init.seeders;

import org.arquiweb.store.application.ports.repositories.InvoiceRepository;
import org.arquiweb.store.domain.models.Invoice;
import org.arquiweb.store.infrastructure.db.init.readers.Reader;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class InvoicesSeeder implements Seeder {

    // Repositorio que se utiliza para guardar en la base de datos
    private final InvoiceRepository repository;
    // Reader encargado de leer los datos del archivo CSV
    private final Reader reader;

    public InvoicesSeeder(InvoiceRepository repository, Reader reader) {
            this.repository = repository;
            this.reader = reader;
    }
    
        public void seed() {

            var rows = reader.read("invoices.csv");
            //List<Invoice> invoices = new ArrayList<>();
            
            rows.forEach(row-> {
                UUID id = UUID.nameUUIDFromBytes(row.get("id").getBytes(StandardCharsets.UTF_8));
                UUID idClient = UUID.nameUUIDFromBytes(row.get("clientId").getBytes(StandardCharsets.UTF_8));


                // Guarda todos los invoice de la lista en la base de datos
                repository.save(new Invoice(id, idClient));
            });
    }

}
