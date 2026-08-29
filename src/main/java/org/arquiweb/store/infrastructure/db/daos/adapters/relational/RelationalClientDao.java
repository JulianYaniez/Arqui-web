package org.arquiweb.store.infrastructure.db.daos.adapters.relational;

import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.domain.models.Client;
import org.arquiweb.store.infrastructure.db.daos.adapters.DaoAdapter;
import org.arquiweb.store.infrastructure.db.engines.Database;
import org.arquiweb.store.infrastructure.db.engines.DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RelationalClientDao extends DaoAdapter implements ClientRepository {

    private final String table = "clients";
    private static RelationalClientDao instance;

    private RelationalClientDao(Database db) {
        super(db);
    }

    public static RelationalClientDao getInstance() {
        if (instance == null) {
            instance = new RelationalClientDao(DatabaseFactory.getRelationalDatabase());
        }
        return instance;
    }

   public Optional<Client> findById(UUID id) {
       String sql = "SELECT * FROM " + this.table + " WHERE id = ?";
       try (
           Connection conn = db.getConnection();
           PreparedStatement stmt = conn.prepareStatement(sql);
       ) {
           stmt.setObject(1, id);
           ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                Client client = new Client(
                        (UUID) rs.getObject("id"),
                        rs.getString("name"),
                        rs.getString("email")
                );

                return Optional.of(client);
            }
       } catch (SQLException e) {
           System.err.println("Client not found: " + e.getMessage());
       }
       return Optional.empty();
   }

   public List<Client> findAll() {
       List<Client> res = new ArrayList<>();
       String sql = "SELECT * FROM " + this.table  + " c " + """ 
                   JOIN invoices i ON c.id = i.clientid
                   JOIN invoice_products ip  ON i.id = ip.invoiceid
                   JOIN products p ON ip.productid = p.id
                   GROUP BY c.id, i.id, ip.invoiceid, ip.productid, p.id
                   ORDER BY count(p.value) DESC;
                   """;

       try(
               Connection conn = db.getConnection();
               PreparedStatement stmt = conn.prepareStatement(sql);
               ResultSet rs = stmt.executeQuery();
       ) {
           while(rs.next()) {
               Client client = new Client(
                       (UUID) rs.getObject("id"),
                       rs.getString("name"),
                       rs.getString("email")
               );
               res.add(client);
           }
       } catch (SQLException e) {
           System.err.println("No clients found: " + e.getMessage());
       }
       return res;
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
           System.err.println("Couldn't save client: " + e.getMessage());
       }
       return client.getId();
   }

   public void saveAll(List<Client> clients) {
        String sql = "INSERT INTO " + this.table + " (id, name, email) VALUES (?, ?, ?)";

        try (
                Connection conn = db.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            conn.setAutoCommit(false);

            for (Client client : clients) {
                stmt.setObject(1, client.getId());
                stmt.setString(2, client.getName());
                stmt.setString(3, client.getEmail());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            System.err.println("Couldn't save clients: " + e.getMessage());
        }
   }
}
