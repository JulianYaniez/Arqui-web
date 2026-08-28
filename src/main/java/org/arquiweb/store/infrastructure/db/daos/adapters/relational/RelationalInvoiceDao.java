package org.arquiweb.store.infrastructure.db.daos.adapters.relational;

import org.arquiweb.store.application.ports.repositories.InvoiceRepository;
import org.arquiweb.store.domain.models.Invoice;
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

public class RelationalInvoiceDao extends DaoAdapter implements InvoiceRepository {

    private final String table = "invoices";
    private static RelationalInvoiceDao instance;

    private RelationalInvoiceDao(Database db) {
        super(db);
    }

    public static RelationalInvoiceDao getInstance() {
        if(instance == null) {
            instance = new RelationalInvoiceDao(DatabaseFactory.getRelationalDatabase());
        }
        return instance;
    }

    public Optional<Invoice> findById(UUID id) {
        String sql = "SELECT * FROM " + this.table + " WHERE id = ?";
        try(
                Connection conn = db.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                Invoice invoice = new Invoice( (UUID) rs.getObject("id"));

                return Optional.of(invoice);
            }
        } catch (SQLException e) {
            System.err.println("Customer not found : " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Invoice>  findAll() {
        String sql = "SELECT * FROM " + this.table;
        List<Invoice> res = new ArrayList<>();
        try (
                Connection conn = db.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        ) {
            while(rs.next()) {
                Invoice invoice = new Invoice (
                        (UUID) rs.getObject("id")
                );
                res.add(invoice);
            }
        } catch (SQLException e) {
            System.err.println("Customer not found : " + e.getMessage());
        }
        return res;
    }

    public UUID save(Invoice client) {
        String sql = "INSERT INTO " + this.table + " VALUES (?, ?)";
        try(
                Connection conn = db.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setObject(1, client.getInvoiceId());
            stmt.setObject(2, client.getClientId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Customer not found : " + e.getMessage());
        }
        return client.getInvoiceId();
    }

    public void saveAll(List<Invoice> invoices) {
        String sql = "INSERT INTO " + this.table + " VALUES (?, ?)";
        try(
                Connection conn = db.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            for(Invoice i : invoices) {
                stmt.setObject(1, i.getInvoiceId());
                stmt.setObject(2, i.getClientId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            System.err.println("Customer not found : " + e.getMessage());
        }
    }
}
