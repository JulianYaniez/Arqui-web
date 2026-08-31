package org.arquiweb.store.application.ports.daos;

import org.arquiweb.store.application.ports.dtos.ClientBillingDTO;
import org.arquiweb.store.domain.models.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientDao {
    Optional<Client> findById(UUID id);
    List<Client> findAll();
    UUID save(Client client);
    void saveAll(List<Client> clients);

    List<ClientBillingDTO> findAllByBilling();
}
