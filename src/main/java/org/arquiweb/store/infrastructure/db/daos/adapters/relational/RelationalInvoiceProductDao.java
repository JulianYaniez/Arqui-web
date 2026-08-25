package org.arquiweb.store.infrastructure.db.daos.adapters.relational;

import org.arquiweb.store.application.ports.repositories.InvoiceProductRepository;
import org.arquiweb.store.application.ports.repositories.ProductRepository;
import org.arquiweb.store.domain.models.InvoiceProduct;
import org.arquiweb.store.domain.models.Product;
import org.arquiweb.store.infrastructure.db.daos.adapters.DaoAdapter;
import org.arquiweb.store.infrastructure.db.engines.Database;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RelationalInvoiceProductDao extends DaoAdapter implements InvoiceProductRepository {

    public RelationalInvoiceProductDao(Database db) {
        super(db);
    }

   public Optional<InvoiceProduct> findById(UUID id) {
       // TODO
       return null;
   }

   public List<InvoiceProduct>  findAll() {
       // TODO
       return null;
   }

   public UUID save(InvoiceProduct client) {
       // TODO
       return null;
   }
}
