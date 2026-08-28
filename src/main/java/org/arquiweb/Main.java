package org.arquiweb;

import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.application.ports.repositories.InvoiceProductRepository;
import org.arquiweb.store.application.ports.repositories.InvoiceRepository;
import org.arquiweb.store.application.ports.repositories.ProductRepository;
import org.arquiweb.store.infrastructure.db.daos.factories.RelationalDaoFactory;
import org.arquiweb.store.infrastructure.db.init.builders.RelationalBuilder;
import org.arquiweb.store.infrastructure.db.init.readers.CsvReader;
import org.arquiweb.store.infrastructure.db.init.seeders.ClientsSeeder;
import org.arquiweb.store.infrastructure.db.init.seeders.InvoiceProductSeeder;
import org.arquiweb.store.infrastructure.db.init.seeders.InvoicesSeeder;
import org.arquiweb.store.infrastructure.db.init.seeders.ProductsSeeder;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        DaoFactory factory = RelationalDaoFactory.getInstance();

        ClientRepository clientRepository = factory.getClientRepository();
        InvoiceRepository invoiceRepository = factory.getInvoiceRepository();
        InvoiceProductRepository invoiceProductRepository = factory.getInvoiceProductRepository();
        ProductRepository productRepository = factory.getProductRepository();

        CsvReader reader = new CsvReader();

        ClientsSeeder clientSeeder = new ClientsSeeder(clientRepository, reader);
        InvoicesSeeder invoicesSeeder = new InvoicesSeeder(invoiceRepository, reader);
        InvoiceProductSeeder invoiceProductSeeder = new InvoiceProductSeeder(invoiceProductRepository, reader);
        ProductsSeeder productsSeeder = new ProductsSeeder(productRepository, reader);

        RelationalBuilder builder = new RelationalBuilder();

        builder.build();
        clientSeeder.seed();
        invoicesSeeder.seed();
        productsSeeder.seed();
        invoiceProductSeeder.seed();
    }
}
