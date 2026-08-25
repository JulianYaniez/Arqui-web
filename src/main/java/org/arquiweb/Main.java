package org.arquiweb;

import org.arquiweb.store.application.services.ClientService;
import org.arquiweb.store.domain.models.Client;
import org.arquiweb.store.application.ports.factories.DaoFactory;
import org.arquiweb.store.infrastructure.db.daos.factories.RelationalDaoFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        DaoFactory factory = RelationalDaoFactory.getInstance();

        ClientService clientService = new ClientService(factory);

        Client c1 = new Client("Pedro", "pedro@gmail·com");

        clientService.insert(c1);
    }
}
