package org.arquiweb.store.infrastructure.db.daos.adapters.relational;

import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.domain.models.Client;
import org.arquiweb.store.infrastructure.db.daos.adapters.DaoAdapter;
import org.arquiweb.store.infrastructure.db.engines.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RelationalClientDao extends DaoAdapter implements ClientRepository {

    private final String table = "clients";

    public RelationalClientDao(Database db) {
        super(db);
    }

   public Optional<Client> findById(UUID id) {
       // TODO
       return null;
   }

   public List<Client>  findAll() {
       // TODO
       return null;
   }

   public UUID save(Client client) {
       String sql = "INSERT INTO " + this.table + " (id, name, email) VALUES (?, ?, ?)";

       try (
               Connection conn = db.getConnection();
               PreparedStatement stmt = conn.prepareStatement(sql);
       ) {
           stmt.setObject(1, client.getId());
           stmt.setString(2, client.getName());
           stmt.setString(3, client.getEmail());
           stmt.executeUpdate();
       } catch (SQLException e) {
           System.err.println("Couldn't save user: " + e.getMessage());
       }
       return client.getId();
   }
}
