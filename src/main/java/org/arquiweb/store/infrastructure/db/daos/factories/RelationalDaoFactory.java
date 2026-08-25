package org.arquiweb.store.infrastructure.db.daos.factories;

import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.application.ports.repositories.InvoiceProductRepository;
import org.arquiweb.store.application.ports.repositories.InvoiceRepository;
import org.arquiweb.store.application.ports.repositories.ProductRepository;
import org.arquiweb.store.infrastructure.db.daos.adapters.relational.RelationalClientDao;
import org.arquiweb.store.infrastructure.db.daos.adapters.relational.RelationalInvoiceDao;
import org.arquiweb.store.infrastructure.db.daos.adapters.relational.RelationalInvoiceProductDao;
import org.arquiweb.store.infrastructure.db.daos.adapters.relational.RelationalProductDao;
import org.arquiweb.store.infrastructure.db.engines.RelationalDatabase;

public class RelationalDaoFactory extends DaoFactory {
    private RelationalDatabase database;

    @Override
    public ClientRepository getClientRepository() {
        return new RelationalClientDao(database);
    }

    @Override
    public ProductRepository getProductRepository() {
        return new RelationalProductDao(database);
    }

    @Override
    public InvoiceRepository  getInvoiceRepository() {
        return new RelationalInvoiceDao(database);
    }

    @Override
    public InvoiceProductRepository getInvoiceProductRepository() {
        return new RelationalInvoiceProductDao(database);
    }
}
