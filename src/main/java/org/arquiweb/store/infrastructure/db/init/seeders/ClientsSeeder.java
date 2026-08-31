package org.arquiweb.store.infrastructure.db.init.seeders;

import org.arquiweb.store.application.ports.daos.ClientDao;
import org.arquiweb.store.domain.models.Client;
import org.arquiweb.store.infrastructure.db.engines.Database;
import org.arquiweb.store.infrastructure.db.engines.DatabaseFactory;
import org.arquiweb.store.infrastructure.db.init.readers.Reader;
import org.arquiweb.store.infrastructure.db.utils.RelationalDatabaseUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientsSeeder implements Seeder {

    
    // Repository that saves data in bbdd
    private final ClientDao repository;
    // Reader who reads csv 
    private final Reader reader;

    public ClientsSeeder(ClientDao repository, Reader reader) {
        this.repository = repository;
        this.reader = reader;
    }

    public void seed() {
        Database db = DatabaseFactory.getRelationalDatabase();
        if (RelationalDatabaseUtils.isTablePopulated(db, "clients"))
            return;

        var rows = reader.read("clients.csv");
        List<Client> clients = new ArrayList<>();

        rows.forEach(row -> {
            UUID id = UUID.nameUUIDFromBytes(row.get("id").getBytes(StandardCharsets.UTF_8));
            clients.add(
                    new Client(
                            id,
                            row.get("name"),
                            row.get("email")
                    )
            );
        });

        // Save clients to the list of bbdd
        repository.saveAll(clients);
    }
}
