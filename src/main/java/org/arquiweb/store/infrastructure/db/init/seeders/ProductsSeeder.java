package org.arquiweb.store.infrastructure.db.init.seeders;

import org.arquiweb.store.application.ports.daos.ProductDao;
import org.arquiweb.store.domain.models.Product;
import org.arquiweb.store.infrastructure.db.engines.Database;
import org.arquiweb.store.infrastructure.db.engines.DatabaseFactory;
import org.arquiweb.store.infrastructure.db.init.readers.Reader;
import org.arquiweb.store.infrastructure.db.utils.RelationalDatabaseUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductsSeeder implements Seeder{

        // Repositorio que se utiliza para guardar los clientes en la base de datos
        private final ProductDao repository;
        // Reader encargado de leer los datos del archivo CSV
        private final Reader reader;


    public ProductsSeeder(ProductDao repository, Reader reader) {
        this.repository = repository;
        this.reader = reader;
    }

    public void seed() {
        Database db = DatabaseFactory.getRelationalDatabase();
        if (RelationalDatabaseUtils.isTablePopulated(db, "products"))
            return;


        var rows = reader.read("products.csv");
        List<Product> products = new ArrayList<>();


        rows.forEach(row -> {
            Integer value = Integer.valueOf(
                    row.get("value")
            );

            UUID id = UUID.nameUUIDFromBytes(row.get("id").getBytes(StandardCharsets.UTF_8));

            products.add(
                    new Product(
                            id,
                            row.get("name"),
                            value
                    )
            );

        });

        // Guarda todos los products de la lista en la base de datos
        repository.saveAll(products);
    }

}
