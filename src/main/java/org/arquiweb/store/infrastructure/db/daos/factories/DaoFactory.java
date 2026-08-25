package org.arquiweb.store.infrastructure.db.daos.factories;

import org.arquiweb.store.application.ports.repositories.ClientRepository;
import org.arquiweb.store.application.ports.repositories.InvoiceProductRepository;
import org.arquiweb.store.application.ports.repositories.InvoiceRepository;
import org.arquiweb.store.application.ports.repositories.ProductRepository;

public abstract class DaoFactory {
   public enum Engines {
       POSTGRES,
   }

   public abstract ClientRepository getClientRepository();

   public abstract ProductRepository getProductRepository();

   public abstract InvoiceRepository getInvoiceRepository();

   public abstract InvoiceProductRepository getInvoiceProductRepository();

}
