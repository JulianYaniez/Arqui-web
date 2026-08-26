package org.arquiweb.store.infrastructure.db.init.seeders;

import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.domain.models.Client;
import org.arquiweb.store.infrastructure.db.init.readers.Reader;

import java.util.ArrayList;
import java.util.List;

public class ClientsSeeder implements Seeder {

    private final ClientRepository repository;
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
                            row.get("name"),
                            row.get("email")
                    )
            );
        });

        repository.saveAll(clients);
    }
}
