package org.arquiweb.store.infrastructure.db.daos.adapters.relational;

import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.domain.models.Client;
import org.arquiweb.store.infrastructure.db.daos.adapters.DaoAdapter;
import org.arquiweb.store.infrastructure.db.engines.Database;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RelationalClientDao extends DaoAdapter implements ClientRepository {

    public RelationalClientDao(Database db) {
        super(db);
    }

   public Optional<Client> findById(UUID id) {
       // TODO
       return null;
   }

   public List<Client>  findAll() {
       // TODO
       return null;
   }

   public UUID save(Client client) {
       // TODO
       return null;
   }
}
