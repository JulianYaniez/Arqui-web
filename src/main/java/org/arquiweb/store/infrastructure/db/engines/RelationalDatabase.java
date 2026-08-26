package org.arquiweb.store.infrastructure.db.engines;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RelationalDatabase extends Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/tpe_group_7";
    private static final String USER = "group_7";
    private static final String PASSWORD = "0112358";

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
