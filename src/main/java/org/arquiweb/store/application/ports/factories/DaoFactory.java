package org.arquiweb.store.application.ports.factories;

import org.arquiweb.store.application.ports.daos.ClientDao;
import org.arquiweb.store.application.ports.daos.InvoiceProductDao;
import org.arquiweb.store.application.ports.daos.InvoiceDao;
import org.arquiweb.store.application.ports.daos.ProductDao;

public abstract class DaoFactory {
   public enum Engines {
       POSTGRES,
   }

   public abstract ClientDao getClientRepository();

   public abstract ProductDao getProductRepository();

   public abstract InvoiceDao getInvoiceRepository();

   public abstract InvoiceProductDao getInvoiceProductRepository();

}
