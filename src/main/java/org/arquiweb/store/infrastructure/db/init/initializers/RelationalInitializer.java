package org.arquiweb.store.infrastructure.db.init.initializers;

import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.infrastructure.db.daos.factories.RelationalDaoFactory;
import org.arquiweb.store.infrastructure.db.init.builders.Builder;
import org.arquiweb.store.infrastructure.db.init.builders.RelationalBuilder;
import org.arquiweb.store.infrastructure.db.init.readers.CsvReader;
import org.arquiweb.store.infrastructure.db.init.seeders.ClientsSeeder;
import org.arquiweb.store.infrastructure.db.init.seeders.InvoiceProductsSeeder;
import org.arquiweb.store.infrastructure.db.init.seeders.InvoicesSeeder;
import org.arquiweb.store.infrastructure.db.init.seeders.ProductsSeeder;

public class RelationalInitializer implements Initializer {

    private static Initializer instance;

    public static Initializer getInstance() {
        if (instance == null) {
            instance = new RelationalInitializer();
        }
        return instance;
    }

    private RelationalInitializer() {
    }

    public void build() {
        Builder builder = RelationalBuilder.getInstance();
        builder.build();
    }

    public void seed() {
        var reader = CsvReader.getInstance();
        DaoFactory factory = RelationalDaoFactory.getInstance();

        var clientRepo = factory.getClientRepository();
        var invoiceRepo = factory.getInvoiceRepository();
        var productRepo = factory.getProductRepository();
        var invoiceProductRepo = factory.getInvoiceProductRepository();

        var clientsSeeder = new ClientsSeeder(clientRepo, reader);
        var invoicesSeeder = new InvoicesSeeder(invoiceRepo, reader);
        var productsSeeder = new ProductsSeeder(productRepo, reader);
        var invoiceProductsSeeder = new InvoiceProductsSeeder(invoiceProductRepo, reader);

        clientsSeeder.seed();
        invoicesSeeder.seed();
        productsSeeder.seed();
        invoiceProductsSeeder.seed();
    }
}
