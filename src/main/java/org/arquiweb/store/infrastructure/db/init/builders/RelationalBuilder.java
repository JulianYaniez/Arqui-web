package org.arquiweb.store.infrastructure.db.init.builders;

import org.arquiweb.store.infrastructure.db.engines.Database;
import org.arquiweb.store.infrastructure.db.engines.DatabaseFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RelationalBuilder implements Builder {

    private final Database database;

    private static Builder instance;


    public static Builder getInstance() {
        if (instance == null) {
            instance = new RelationalBuilder();
        }
        return instance;
    }

    private RelationalBuilder() {
        this.database = DatabaseFactory.getRelationalDatabase();
    }

    public void build() {
        String client = """
                    CREATE TABLE IF NOT EXISTS clients (
                        id UUID PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(100) UNIQUE NOT NULL
                    );
                    """;

        String product = """
                CREATE TABLE IF NOT EXISTS products (
                    id UUID PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    value INT NOT NULL
                );
                """;

        String invoice = """
                CREATE TABLE IF NOT EXISTS invoices (
                    id UUID PRIMARY KEY,
                    clientId UUID NOT NULL,
                    FOREIGN KEY (clientId) REFERENCES clients(id)
                );
        """;

        String invoiceProduct = """
                CREATE TABLE IF NOT EXISTS invoice_products (
                    invoiceId UUID NOT NULL,
                    productId UUID NOT NULL,
                    quantity int NOT NULL,
                    PRIMARY KEY (invoiceId, productId),
                    FOREIGN KEY (invoiceId) REFERENCES invoices(id),
                    FOREIGN KEY (productId) REFERENCES products(id)
                );
                """;

        List<String> tables = new ArrayList<>();
        tables.add(client);
        tables.add(product);
        tables.add(invoice);
        tables.add(invoiceProduct);

        try (
                Connection conn = database.getConnection();
                Statement stmt = conn.createStatement();
                ) {

            conn.setAutoCommit(false);

            for (String table : tables) {
                stmt.execute(table);
            }

            conn.commit();

        } catch (SQLException ex ) {
            System.err.println("Error creating database tables.");
        }
    }
}
