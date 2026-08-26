package org.arquiweb.store.infrastructure.db.init.readers;

import java.util.List;
import java.util.Map;

public interface Reader {

    String basePath = "src/main/resources/";

    List<Map<String, String>> read(String filename);
}
