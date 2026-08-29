package org.arquiweb;

import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.application.services.ClientService;
import org.arquiweb.store.application.services.ProductService;
import org.arquiweb.store.domain.models.Client;
import org.arquiweb.store.infrastructure.db.daos.factories.RelationalDaoFactory;
import org.arquiweb.store.infrastructure.db.init.initializers.Initializer;
import org.arquiweb.store.infrastructure.db.init.initializers.RelationalInitializer;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main() {
        Initializer initializer = RelationalInitializer.getInstance();

        initializer.build(); // Act 1.

        initializer.seed(); // Act 2.

        DaoFactory factory = RelationalDaoFactory.getInstance();


        var productService = new ProductService(factory);
        var topProduct = productService.getTopRevenue();
        System.out.println("Top product:");
        System.out.println(topProduct.toString());
        System.out.println();

        System.out.println("List of clients billed the most:");
        var clientService = new ClientService(factory);
        var clientList = clientService.getAll();
        printAll(clientList);

    }

    private static void printAll(List<Client> list) {
        for (Client client : list) {
            System.out.println(client.toString());
        }

    }
}
