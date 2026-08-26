package org.arquiweb;

import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.infrastructure.db.daos.factories.RelationalDaoFactory;
import org.arquiweb.store.infrastructure.db.init.builders.RelationalBuilder;
import org.arquiweb.store.infrastructure.db.init.readers.CsvReader;
import org.arquiweb.store.infrastructure.db.init.seeders.ClientsSeeder;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        DaoFactory factory = RelationalDaoFactory.getInstance();

        ClientRepository clientRepository = factory.getClientRepository();

        CsvReader reader = new CsvReader();

        ClientsSeeder seeder = new ClientsSeeder(
                clientRepository,
                reader
        );

        RelationalBuilder builder = new RelationalBuilder();

        builder.build();

        // seeder.seed();
    }
}
