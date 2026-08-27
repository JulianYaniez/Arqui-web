package org.arquiweb.store.infrastructure.db.daos.adapters.relational;

import org.arquiweb.store.application.ports.repositories.InvoiceRepository;
import org.arquiweb.store.domain.models.Invoice;
import org.arquiweb.store.infrastructure.db.daos.adapters.DaoAdapter;
import org.arquiweb.store.infrastructure.db.engines.Database;
import org.arquiweb.store.infrastructure.db.engines.DatabaseFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RelationalInvoiceDao extends DaoAdapter implements InvoiceRepository {

    private static RelationalInvoiceDao instance;

    private RelationalInvoiceDao(Database db) {
        super(db);
    }

    public static RelationalInvoiceDao getInstance() {
        if(instance == null) {
            instance = new RelationalInvoiceDao(DatabaseFactory.getRelationalDatabase());
        }
        return instance;
    }

    public Optional<Invoice> findById(UUID id) {
        // TODO
        return null;
    }

    public List<Invoice>  findAll() {
        // TODO
        return null;
    }

    public UUID save(Invoice client) {
        // TODO
        return null;
    }
}
