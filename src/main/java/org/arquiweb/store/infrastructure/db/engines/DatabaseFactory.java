package org.arquiweb.store.infrastructure.db.engines;

public class DatabaseFactory {

    private static Database relationalDatabase;

    private DatabaseFactory() {}

    public static Database getRelationalDatabase() {
        if (relationalDatabase == null) {
            relationalDatabase = new RelationalDatabase();
        }
        return relationalDatabase;
    }
}
