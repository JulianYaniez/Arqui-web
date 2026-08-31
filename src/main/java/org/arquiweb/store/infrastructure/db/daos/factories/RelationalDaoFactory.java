package org.arquiweb.store.infrastructure.db.daos.factories;

import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.application.ports.daos.ClientDao;
import org.arquiweb.store.application.ports.daos.InvoiceProductDao;
import org.arquiweb.store.application.ports.daos.InvoiceDao;
import org.arquiweb.store.application.ports.daos.ProductDao;
import org.arquiweb.store.infrastructure.db.daos.adapters.relational.RelationalClientDao;
import org.arquiweb.store.infrastructure.db.daos.adapters.relational.RelationalInvoiceDao;
import org.arquiweb.store.infrastructure.db.daos.adapters.relational.RelationalInvoiceProductDao;
import org.arquiweb.store.infrastructure.db.daos.adapters.relational.RelationalProductDao;

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
    public ClientDao getClientRepository() { return RelationalClientDao.getInstance(); }

    @Override
    public ProductDao getProductRepository() {
        return RelationalProductDao.getInstance();
    }

    @Override
    public InvoiceDao getInvoiceRepository() {
        return RelationalInvoiceDao.getInstance();
    }

    @Override
    public InvoiceProductDao getInvoiceProductRepository() { return RelationalInvoiceProductDao.getInstance(); }
}
