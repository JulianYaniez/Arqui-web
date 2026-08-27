package org.arquiweb.store.infrastructure.db.daos.factories;

import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.application.ports.repositories.InvoiceProductRepository;
import org.arquiweb.store.application.ports.repositories.InvoiceRepository;
import org.arquiweb.store.application.ports.repositories.ProductRepository;
import org.arquiweb.store.infrastructure.db.daos.adapters.relational.RelationalClientDao;
import org.arquiweb.store.infrastructure.db.daos.adapters.relational.RelationalInvoiceDao;
import org.arquiweb.store.infrastructure.db.daos.adapters.relational.RelationalInvoiceProductDao;
import org.arquiweb.store.infrastructure.db.daos.adapters.relational.RelationalProductDao;
import org.arquiweb.store.infrastructure.db.engines.Database;
import org.arquiweb.store.infrastructure.db.engines.DatabaseFactory;
import org.arquiweb.store.infrastructure.db.engines.RelationalDatabase;

public class RelationalDaoFactory extends DaoFactory {
    private static DaoFactory instance;

    private RelationalDaoFactory() {}

    public static DaoFactory getInstance() {
        if (instance == null) {
            instance = new RelationalDaoFactory();
        }
        return instance;
    }

    @Override
    public ClientRepository getClientRepository() { return RelationalClientDao.getInstance(); }

    @Override
    public ProductRepository getProductRepository() {
        return RelationalProductDao.getInstance();
    }

    @Override
    public InvoiceRepository  getInvoiceRepository() {
        return RelationalInvoiceDao.getInstance();
    }

    @Override
    public InvoiceProductRepository getInvoiceProductRepository() { return RelationalInvoiceProductDao.getInstance(); }
}
