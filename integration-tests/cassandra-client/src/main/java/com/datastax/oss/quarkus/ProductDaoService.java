package com.datastax.oss.quarkus;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;

import com.datastax.oss.quarkus.dao.InventoryMapperBuilder;
import com.datastax.oss.quarkus.dao.ProductDao;

@ApplicationScoped
public class ProductDaoService {

    private final ProductDao dao;

    @Inject
    public ProductDaoService(CqlSession session) {

        session.execute(
                "CREATE KEYSPACE IF NOT EXISTS k1 WITH replication "
                        + "= {'class':'SimpleStrategy', 'replication_factor':1};");

        session.execute("CREATE TABLE IF NOT EXISTS k1.product(id uuid PRIMARY KEY, description text)");
        dao = new InventoryMapperBuilder(session).build().productDao(CqlIdentifier.fromCql("k1"));
    }

    ProductDao getDao() {
        return dao;
    }

}
