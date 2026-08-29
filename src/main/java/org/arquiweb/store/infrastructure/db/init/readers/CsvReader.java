package org.arquiweb.store.infrastructure.db.init.readers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class CsvReader implements Reader {

    private static Reader instance;

    public static Reader getInstance() {
        if (instance == null) {
            instance = new CsvReader();
        }
        return instance;
    }

    public List<Map<String, String>> read(String file) {

        Path path = Path.of(basePath + "csv/" + file);

        try (Stream<String> lines = Files.lines(path)) {
            Iterator<String> iterator = lines.iterator();

            // Return early if empty
            if (!iterator.hasNext()) {
                return List.of();
            }

            // Get Column names
            String[] header = iterator.next().split(",");


            List<Map<String, String>> rows = new ArrayList<>();

            while (iterator.hasNext()) {
                String line = iterator.next().trim();
                if (line.isEmpty()) continue;

                String[] values = line.split(",");

                Map<String, String> row = new HashMap<>();

                for (int i = 0; i < header.length; i++) {
                    // Check for missing columns, avoid OutOfBounds
                    String val = (i < values.length) ? values[i].trim() : "";
                    row.put(header[i], values[i]);
                }

                rows.add(row);
            }

            return rows;
        } catch (IOException e) {
            System.err.println("Error reading file '" + path.toString() + "'");
            return List.of();
        }
    }
}
