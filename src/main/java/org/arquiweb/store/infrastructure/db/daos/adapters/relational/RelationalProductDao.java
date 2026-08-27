package org.arquiweb.store.infrastructure.db.daos.adapters.relational;

import org.arquiweb.store.application.ports.repositories.ProductRepository;
import org.arquiweb.store.domain.models.Product;
import org.arquiweb.store.infrastructure.db.daos.adapters.DaoAdapter;
import org.arquiweb.store.infrastructure.db.engines.Database;
import org.arquiweb.store.infrastructure.db.engines.DatabaseFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

    public class RelationalProductDao extends DaoAdapter implements ProductRepository {
        private static RelationalProductDao instance;
        public RelationalProductDao(Database db) {
            super(db);
        }

    public static RelationalProductDao getInstance() {
        if(instance == null) {
            instance = new RelationalProductDao(DatabaseFactory.getRelationalDatabase());
        }
        return instance;
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
