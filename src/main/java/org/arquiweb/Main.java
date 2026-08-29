package org.arquiweb;

import org.arquiweb.store.infrastructure.db.init.initializers.Initializer;
import org.arquiweb.store.infrastructure.db.init.initializers.RelationalInitializer;

public class Main {
    static void main() {
        Initializer initializer = RelationalInitializer.getInstance();

        initializer.build(); // Act 1.

        initializer.seed(); // Act 2.


    }
}
