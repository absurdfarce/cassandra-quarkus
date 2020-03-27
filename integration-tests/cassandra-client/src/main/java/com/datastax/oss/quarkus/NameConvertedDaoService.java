package com.datastax.oss.quarkus;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;

import com.datastax.oss.quarkus.dao.nameconverters.NameConverterEntityDao;
import com.datastax.oss.quarkus.dao.nameconverters.TestMapperBuilder;

@ApplicationScoped
public class NameConvertedDaoService {

    private final NameConverterEntityDao dao;

    @Inject
    public NameConvertedDaoService(CqlSession session) {
        session.execute(
                "CREATE KEYSPACE IF NOT EXISTS k1 WITH replication "
                        + "= {'class':'SimpleStrategy', 'replication_factor':1};");

        session.execute("CREATE TABLE IF NOT EXISTS k1.test_NameConverterEntity(test_entityId int primary key)");
        dao = new TestMapperBuilder(session).build().nameConverterEntityDao(CqlIdentifier.fromCql("k1"));
    }

    NameConverterEntityDao getDao() {
        return dao;
    }
}
