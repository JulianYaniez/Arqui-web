package org.arquiweb.store.infrastructure.db.daos.adapters.relational;

import org.arquiweb.store.application.ports.dtos.ProductRevenueDTO;
import org.arquiweb.store.application.ports.daos.ProductDao;
import org.arquiweb.store.domain.models.Product;
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

    public class RelationalProductDao extends DaoAdapter implements ProductDao {
        private final String table = "products";
        private static RelationalProductDao instance;
        public RelationalProductDao(Database db) {
            super(db);
        }

    public static RelationalProductDao getInstance() {
        if(instance == null) {
            instance = new RelationalProductDao(DatabaseFactory.getRelationalDatabase());
        }
        return instance;
    }


   public Optional<Product> findById(UUID id) {
       String sql = "SELECT * FROM " + this.table + " WHERE id = ?";
       try (
               Connection conn = db.getConnection();
               PreparedStatement stmt = conn.prepareStatement(sql);
       ) {
           stmt.setObject(1, id);
           ResultSet rs = stmt.executeQuery();

           if(rs.next()) {
               Product product = new Product(
                    (UUID) rs.getObject("id"),
                    rs.getString("name"),
                    rs.getInt("value")
               );

               return Optional.of(product);
           }
       } catch (SQLException e) {
           System.err.println("Product not found: " + e.getMessage());
       }
       return Optional.empty();
   }

   public List<Product>  findAll() {
       List<Product> res = new ArrayList<>();
       String sql = "SELECT * FROM " + this.table;
       try(
               Connection conn = db.getConnection();
               PreparedStatement stmt = conn.prepareStatement(sql);
               ResultSet rs = stmt.executeQuery();
       ) {
           while(rs.next()) {
               Product product = new Product(
                       (UUID) rs.getObject("id"),
                       rs.getString("name"),
                       rs.getInt("value")
               );
               res.add(product);
           }
       } catch (SQLException e) {
           System.err.println("Product not found: " + e.getMessage());
       }
       return res;
   }

   public UUID save(Product product) {
       String sql = "INSERT INTO " + this.table + " (id, name, value) VALUES (?, ?, ?)";

       try (
               Connection conn = db.getConnection();
               PreparedStatement stmt = conn.prepareStatement(sql);
       ) {
           stmt.setObject(1, product.getId());
           stmt.setString(2, product.getName());
           stmt.setInt(3, product.getValue());
           stmt.executeUpdate();
       } catch (SQLException e) {
           System.err.println("Couldn't save product: " + e.getMessage());
       }
       return product.getId();
   }

   @Override
   public void saveAll(List<Product> products) {
       String sql = "INSERT INTO " + this.table + " (id, name, value) VALUES (?, ?, ?)";

       try (
               Connection conn = db.getConnection();
               PreparedStatement stmt = conn.prepareStatement(sql)
       ) {
           conn.setAutoCommit(false);

           for (Product product : products) {
               stmt.setObject(1, product.getId());
               stmt.setString(2, product.getName());
               stmt.setInt(3, product.getValue());
               stmt.addBatch();
           }

           stmt.executeBatch();
           conn.commit();

       } catch (SQLException e) {
           System.err.println("Couldn't save product: " + e.getMessage());
       }
   }


   @Override
   public Optional<ProductRevenueDTO> findTopRevenueProduct(){
    String sql = "SELECT p.id, p.name, p.value, sum(ip.quantity * p.value) as total_revenue FROM " + this.table +
                  " p JOIN invoice_products ip on p.id = productId GROUP BY p.id, p.name ORDER BY total_revenue LIMIT 1 ";

       try (
               Connection conn = db.getConnection();
               PreparedStatement stmt = conn.prepareStatement(sql);
       ) {
           ResultSet rs = stmt.executeQuery();
           if(rs.next()) {
               ProductRevenueDTO product = new ProductRevenueDTO(
                       (UUID) rs.getObject("id"),
                       rs.getString("name"),
                       rs.getInt("value"),
                       rs.getInt("total_revenue")
               );

               return Optional.of(product);
           }
       } catch (SQLException e) {
           System.err.println("Product not found: " + e.getMessage());
       }
       return Optional.empty();
    }

    }
