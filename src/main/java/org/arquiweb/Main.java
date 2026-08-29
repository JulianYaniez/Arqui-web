package org.arquiweb;

import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.application.ports.repositories.InvoiceProductRepository;
import org.arquiweb.store.application.ports.repositories.InvoiceRepository;
import org.arquiweb.store.application.ports.repositories.ProductRepository;
import org.arquiweb.store.infrastructure.db.daos.factories.RelationalDaoFactory;
import org.arquiweb.store.infrastructure.db.init.builders.RelationalBuilder;
import org.arquiweb.store.infrastructure.db.init.initializers.Initializer;
import org.arquiweb.store.infrastructure.db.init.initializers.RelationalInitializer;
import org.arquiweb.store.infrastructure.db.init.readers.CsvReader;
import org.arquiweb.store.infrastructure.db.init.seeders.ClientsSeeder;
import org.arquiweb.store.infrastructure.db.init.seeders.InvoiceProductsSeeder;
import org.arquiweb.store.infrastructure.db.init.seeders.InvoicesSeeder;
import org.arquiweb.store.infrastructure.db.init.seeders.ProductsSeeder;

public class Main {
    static void main() {
        Initializer initializer = RelationalInitializer.getInstance();

        initializer.build(); // Act 1.

        initializer.seed(); // Act 2.


    }
}
