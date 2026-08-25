package org.arquiweb.store.infrastructure.db.daos.adapters;

import org.arquiweb.store.infrastructure.db.engines.Database;

public abstract class DaoAdapter {
    protected Database db;

    public DaoAdapter(Database db) {
        this.db = db;
    }
}
