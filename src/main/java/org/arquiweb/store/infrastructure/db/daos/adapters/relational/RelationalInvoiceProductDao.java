package org.arquiweb.store.infrastructure.db.daos.adapters.relational;

import org.arquiweb.store.application.ports.repositories.InvoiceProductRepository;
import org.arquiweb.store.domain.models.Client;
import org.arquiweb.store.domain.models.InvoiceProduct;
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

public class RelationalInvoiceProductDao extends DaoAdapter implements InvoiceProductRepository {
    private final String table = "invoice_products";
    private static RelationalInvoiceProductDao instance;
    public RelationalInvoiceProductDao(Database db) {
        super(db);
    }

    public static RelationalInvoiceProductDao getInstance() {
        if(instance == null) {
            instance = new RelationalInvoiceProductDao(DatabaseFactory.getRelationalDatabase());
        }
        return instance;
    }

   public Optional<InvoiceProduct> findById(UUID id) {
        String sql = "SELECT * from" + this.table + "where id = ?";
        try (
             Connection conn =  db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setObject(1,id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                InvoiceProduct invoiceProduct = new InvoiceProduct(
                        (UUID) rs.getObject("invoiceId"),
                        (UUID) rs.getObject("productId"),
                        rs.getInt("quantity")
                );
                return Optional.of(invoiceProduct);
            }



        }catch (SQLException e){
            System.err.println(" not found : " + e.getMessage());
        }
       return Optional.empty();
   }

   public List<InvoiceProduct>  findAll() {
        List<InvoiceProduct> res = new ArrayList<>();
        String sql = "SELECT * FROM " + this.table;
        try(
                Connection conn =  db.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        ){
            while (rs.next()){
                InvoiceProduct invoiceProduct = new InvoiceProduct(
                        (UUID) rs.getObject("invoiceId"),
                        (UUID) rs.getObject("productId"),
                        rs.getInt("quantity")
                );
                res.add(invoiceProduct);
            }

        }catch (SQLException e){
            System.out.println(e);
        }
        return  res;
   }

   public UUID save(InvoiceProduct invoicesProduct) {
        String sql = "INSERT INTO " + this.table + " (invoiceId, productId, quantity) VALUES (?, ?, ?)";
        try(
                Connection conn = db.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ){
            stmt.setObject(1,invoicesProduct.getInvoiceId());
            stmt.setObject(2,invoicesProduct.getProductId());
            stmt.setObject(3,invoicesProduct.getQuantity());
            stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("couldn´t save invoice producto" + e);
        }
        return invoicesProduct.getInvoiceId();
   }

    @Override
    public List<InvoiceProduct> saveAll(List<InvoiceProduct> invoicesProducts) {
        String sql = "INSERT INTO " + this.table + " (invoiceId, productId, quantity) VALUES (?, ?, ?)";

        try (
                Connection conn = db.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            conn.setAutoCommit(false);

            for (InvoiceProduct invoicesProduct: invoicesProducts) {
                stmt.setObject(1,invoicesProduct.getInvoiceId());
                stmt.setObject(2,invoicesProduct.getProductId());
                stmt.setObject(3,invoicesProduct.getQuantity());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            System.err.println("Couldn't save users: " + e.getMessage());
        }
        return null;
    }
}
