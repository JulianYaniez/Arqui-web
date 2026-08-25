package org.arquiweb.store.infrastructure.db.daos.adapters.relational;

import org.arquiweb.store.application.ports.repositories.ProductRepository;
import org.arquiweb.store.domain.models.Product;
import org.arquiweb.store.infrastructure.db.daos.adapters.DaoAdapter;
import org.arquiweb.store.infrastructure.db.engines.Database;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RelationalProductDao extends DaoAdapter implements ProductRepository {

    public RelationalProductDao(Database db) {
        super(db);
    }

   public Optional<Product> findById(UUID id) {
       // TODO
       return null;
   }

   public List<Product>  findAll() {
       // TODO
       return null;
   }

   public UUID save(Product client) {
       // TODO
       return null;
   }
}
