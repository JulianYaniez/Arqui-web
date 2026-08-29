package org.arquiweb.store.infrastructure.db.utils;

import org.arquiweb.store.infrastructure.db.engines.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RelationalDatabaseUtils {

    public static boolean isTablePopulated(Database db, String tableName) {
        String sql = "SELECT EXISTS (SELECT 1 FROM " + tableName + ")";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBoolean(1);
            }
            System.out.println("b");

        } catch (SQLException e) {
            throw new RuntimeException("Error verifying if " + tableName + " has data", e);
        }

        System.out.println("c");

        return false;
    }
}
