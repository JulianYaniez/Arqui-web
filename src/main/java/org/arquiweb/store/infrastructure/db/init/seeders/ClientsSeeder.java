package org.arquiweb.store.infrastructure.db.init.seeders;

import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.domain.models.Client;
import org.arquiweb.store.infrastructure.db.init.readers.Reader;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientsSeeder implements Seeder {

    
    // Repositorio que se utiliza para guardar en la base de datos
    private final ClientRepository repository;
    // Reader encargado de leer los datos del archivo CSV
    private final Reader reader;

    public ClientsSeeder(ClientRepository repository, Reader reader) {
        this.repository = repository;
        this.reader = reader;
    }

    public void seed() {
        var rows = reader.read("clients.csv");
        List<Client> clients = new ArrayList<>();

        rows.forEach(row -> {
            clients.add(
                    new Client(
                            UUID.fromString(row.get("id")),
                            row.get("name"),
                            row.get("email")
                    )
            );
        });

        // Guarda todos los clients de la lista en la base de datos
        repository.saveAll(clients);
    }
}
