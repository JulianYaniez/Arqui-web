package org.arquiweb.store.application.services;

import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.domain.models.Client;

import java.util.UUID;

public class ClientService {
    private ClientRepository clientRepository;

    public ClientService() {
        // TODO - Get repository depending on wanted DB
    }


    public static void insert(Client client) {
        // TODO
    }

    public static void get(UUID id) {
        // TODO
    }

    public static void getAll() {
        // TODO
    }
}
