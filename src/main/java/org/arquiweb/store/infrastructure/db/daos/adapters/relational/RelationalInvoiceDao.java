package org.arquiweb.store.infrastructure.db.daos.adapters.relational;

import org.arquiweb.store.application.ports.daos.InvoiceDao;
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

public class RelationalInvoiceDao extends DaoAdapter implements InvoiceDao {

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
            System.err.println("Couldn't find invoice: " + e.getMessage());
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
            System.err.println("Couldn't find invoices: " + e.getMessage());
        }
        return res;
    }

    public UUID save(Invoice invoice) {
        String sql = "INSERT INTO " + this.table + " VALUES (?, ?)";
        try(
                Connection conn = db.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setObject(1, invoice.getId());
            stmt.setObject(2, invoice.getClientId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Couldn't save invoice: " + e.getMessage());
        }
        return invoice.getId();
    }

    public void saveAll(List<Invoice> invoices) {
        String sql = "INSERT INTO " + this.table + " VALUES (?, ?)";
        try(
                Connection conn = db.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            conn.setAutoCommit(false);

            for(Invoice i : invoices) {
                stmt.setObject(1, i.getId());
                stmt.setObject(2, i.getClientId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            System.err.println("Couldn't save invoices: " + e.getMessage());
        }
    }
}
