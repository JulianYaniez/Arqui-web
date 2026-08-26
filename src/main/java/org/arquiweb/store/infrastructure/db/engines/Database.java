package org.arquiweb.store.infrastructure.db.engines;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {

    public abstract Connection getConnection() throws SQLException;

}
